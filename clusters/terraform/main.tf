# =================================================================
# Terraform 및 Provider 설정
# =================================================================

terraform {
  backend "s3" {
    # OCI Object Storage는 S3와 호환되므로 's3' 백엔드를 사용합니다.
    bucket                      = "tf_state_bucket" # 👈 1. 생성할 버킷 이름
    key                         = "k0s-cluster/terraform.tfstate" # 상태 파일이 저장될 경로
    region                      = "ap-chuncheon-1" # 👈 2. 버킷이 생성된 리전
    endpoint                    = "https://axsvfekd8pf8.compat.objectstorage.ap-chuncheon-1.oraclecloud.com" # 👈 3. 테넌시 네임스페이스로 수정
    skip_region_validation      = true
    skip_credentials_validation = true
    force_path_style            = true
  }

  required_providers {
    oci = {
      source  = "oracle/oci"
      version = ">= 5.0.0"
    }
  }
}

# =================================================================
# 변수 정의 (입력값)
# =================================================================

# -- OCI 인증 정보 (민감) --
variable "tenancy_ocid" {
  description = "OCI Tenancy's OCID"
  type        = string
  sensitive   = true
}
variable "user_ocid" {
  description = "OCI User's OCID"
  type        = string
  sensitive   = true
}
variable "fingerprint" {
  description = "API Key's Fingerprint"
  type        = string
  sensitive   = true
}
variable "private_key_path" {
  description = "Path to the OCI API private key file"
  type        = string
  sensitive   = true
}

# -- OCI 환경 정보 --
variable "region" {
  description = "The OCI region to create resources in"
  type        = string
}
variable "compartment_ocid" {
  description = "The OCID of the compartment to create resources in"
  type        = string
}

# -- 인스턴스 설정 --
variable "ssh_public_key" {
  description = "Public SSH key to be used for instance access"
  type        = string
  sensitive   = true # 공개키 자체는 민감하지 않지만, 관련된 정보이므로 표시
}
variable "instance_ocpus" {
  description = "Instance OCPU count"
  type        = number
  default     = 4
}
variable "instance_memory_in_gbs" {
  description = "Instance memory in GB"
  type        = number
  default     = 24
}

# OCI 접속을 위한 Provider 설정
provider "oci" {
  region           = var.region
  tenancy_ocid     = var.tenancy_ocid
  user_ocid        = var.user_ocid
  fingerprint      = var.fingerprint
  private_key_path = var.private_key_path
}

# =================================================================
# 네트워크 스택 (VCN, Subnet, Gateway, Route Table)
# =================================================================

# 가상 클라우드 네트워크 (VCN) 생성
resource "oci_core_virtual_network" "k0s_vcn" {
  cidr_block     = "10.0.0.0/16"
  compartment_id = var.compartment_ocid
  display_name   = "k0s-vcn"
  dns_label      = "k0svcn"
}

# 인터넷 게이트웨이 생성
resource "oci_core_internet_gateway" "k0s_internet_gateway" {
  compartment_id = var.compartment_ocid
  display_name   = "k0s-ig"
  vcn_id         = oci_core_virtual_network.k0s_vcn.id
}

# 라우팅 테이블 생성 (모든 외부 트래픽을 인터넷 게이트웨이로)
resource "oci_core_route_table" "k0s_route_table" {
  compartment_id = var.compartment_ocid
  vcn_id         = oci_core_virtual_network.k0s_vcn.id
  display_name   = "k0s-route-table"

  route_rules {
    destination       = "0.0.0.0/0"
    destination_type  = "CIDR_BLOCK"
    network_entity_id = oci_core_internet_gateway.k0s_internet_gateway.id
  }
}

# 보안 목록 (방화벽 규칙) 생성
resource "oci_core_security_list" "k0s_security_list" {
  compartment_id = var.compartment_ocid
  vcn_id         = oci_core_virtual_network.k0s_vcn.id
  display_name   = "k0s-security-list"

  # Egress (나가는 트래픽): 모든 트래픽 허용
  egress_security_rules {
    protocol    = "all"
    destination = "0.0.0.0/0"
  }

  # Ingress (들어오는 트래픽): SSH, HTTP, HTTPS 허용
  ingress_security_rules {
    protocol = "6" # TCP
    source   = "0.0.0.0/0"
    tcp_options {
      min = 22
      max = 22
    }
  }
  ingress_security_rules {
    protocol = "6" # TCP
    source   = "0.0.0.0/0"
    tcp_options {
      min = 80
      max = 80
    }
  }
  ingress_security_rules {
    protocol = "6" # TCP
    source   = "0.0.0.0/0"
    tcp_options {
      min = 443
      max = 443
    }
  }
}

# 서브넷 생성
resource "oci_core_subnet" "k0s_subnet" {
  cidr_block        = "10.0.1.0/24"
  display_name      = "k0s-subnet"
  dns_label         = "k0ssubnet"
  security_list_ids = [oci_core_security_list.k0s_security_list.id]
  compartment_id    = var.compartment_ocid
  vcn_id            = oci_core_virtual_network.k0s_vcn.id
  route_table_id    = oci_core_route_table.k0s_route_table.id
  dhcp_options_id   = oci_core_virtual_network.k0s_vcn.default_dhcp_options_id
}

# =================================================================
# 컴퓨트 인스턴스 (VM)
# =================================================================

# 사용할 OS 이미지를 찾기 위한 데이터 소스
data "oci_core_images" "ubuntu_images" {
  compartment_id           = var.compartment_ocid
  operating_system         = "Canonical Ubuntu"
  operating_system_version = "24.04"
  shape                    = "VM.Standard.A1.Flex"
  sort_by                  = "TIMECREATED"
  sort_order               = "DESC"
}

# 단일 VM 인스턴스 생성
resource "oci_core_instance" "k0s_node" {
  availability_domain = data.oci_identity_availability_domain.ad.name
  compartment_id      = var.compartment_ocid
  display_name        = "k0s-node-01"
  shape               = "VM.Standard.A1.Flex"

  shape_config {
    ocpus         = var.instance_ocpus
    memory_in_gbs = var.instance_memory_in_gbs
  }

  create_vnic_details {
    subnet_id        = oci_core_subnet.k0s_subnet.id
    display_name     = "primary-vnic"
    assign_public_ip = true
    hostname_label   = "k0s-node-01"
  }

  source_details {
    source_type             = "image"
    source_id               = data.oci_core_images.ubuntu_images.images[0].id
    boot_volume_size_in_gbs = 50
  }

  metadata = {
    ssh_authorized_keys = var.ssh_public_key
  }
}

# 사용 가능한 가용 도메인을 찾기 위한 데이터 소스
data "oci_identity_availability_domain" "ad" {
  compartment_id = var.tenancy_ocid
  ad_number      = 1
}

# =================================================================
# 출력 (Outputs)
# =================================================================

# 생성된 인스턴스의 공인 IP를 출력
output "instance_public_ip" {
  value = oci_core_instance.k0s_node.public_ip
}
