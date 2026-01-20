# k0s 기반 K8s 클러스터 및 GitOps 파이프라인 구축

**경량화된 Kubernetes(k0s)를 활용하여 온프레미스 환경을 시뮬레이션하고, IaC 및 GitOps를 도입하여 배포 운영 자동화를 구현한 인프라 엔지니어링 프로젝트입니다.**  

**소개 GIF**
<br/>
<p align="center">
  <img src="img/work.gif" alt="프로젝트 데모 GIF" width="80%"/>
</p>
<p align="center">
  <img src="img/page.PNG" alt="프로젝트 스크린샷" width="80%"/>
</p>
<p align="center">
  <img src="img/grafana.PNG" alt="그라파나 스크린샷" width="80%"/>
</p>

<br/>

## 📚 목차 (Table of Contents)

1.  [프로젝트 개요 (Overview)](#1-프로젝트-개요-overview)
2.  [주요 기능 (Features)](#2-주요-기능-features)
3.  [기술 스택 (Tech Stack)](#3-기술-스택-tech-stack)
4.  [아키텍처 (Architecture)](#4-아키텍처-architecture)
    - [시스템 구성 및 서비스 흐름](#시스템-구성-및-설계-의도)
    - [인프라 구성 및 자동화](#인프라-구성-및-자동화)
    - [CI/CD 파이프라인 (GitOps)](#cicd-파이프라인-gitops)
5.  [트러블슈팅 및 학습 과정 (Troubleshooting)](#5-트러블슈팅-및-학습-과정-troubleshooting)
6.  [향후 개선 계획 (Future Plan)](#6-향후-개선-계획-future-plan)

<br/>

## 1. 프로젝트 개요 (Overview)

-   **개발 기간**: 2025.06.27 ~ 2025.08.21
-   **개발 인원**: 2명 (FE 1명, BE 1명)
-   **주요 목표**:
    - On-Premise 시뮬레이션: 베어메탈 성격의 k0s 클러스터 구축
    - Ops 자동화: Terraform, Ansible, GitOps(FluxCD)를 활용한 운영의 코드화 구현
    - AI 인프라 확장성 확보: 향후 MLOps 파이프라인 수용을 고려한 확장 가능한 아키텍처 설계

<br/>

## 2. 주요 기능 (Features)
> **범례**: 🟢 구현 완료, 🟡 개발 진행 중, 🔴 구상 및 설계 단계

#### 인프라 구축 및 CI/CD

Focus: 애플리케이션 기능 구현보다는 안정적인 서비스 운영을 위한 기반 환경(Platform) 구축에 집중했습니다.

1. 🟢 경량화 클러스터 구축 (Cluster Provisioning)

    - k0s 도입: System Resource가 제한적인 환경을 고려하여 경량 K8s 배포판인 k0s 선정 및 구축

    - 가용성 확보: 단일 노드 내 ReplicaSet 구성을 통한 Pod 레벨의 고가용성(HA) 전략 수립

2. 🟢 GitOps 기반 배포 자동화 (Continuous Deployment)

    - FluxCD 도입: 클러스터 상태를 Git 리포지토리와 실시간 동기화(Reconciliation)하는 Pull 방식 배포 구현

    - Version Control: GitHub Actions와 연동하여 이미지 태그 변경 시 즉각적인 배포 파이프라인 트리거

3. 🟢 보안 및 네트워크 최적화 (Network & Security):

    - HostNetwork 모드 적용: L4 로드밸런서가 없는 환경을 극복하기 위해 Ingress Controller 네트워크 모드 최적화

    - TLS 자동화: Cert-Manager를 활용한 인증서 발급 및 HTTPS 암호화 통신 구현

4. 🟡 IaC 도입 및 고도화 (Infrastructure as Code)

    - Terraform/Ansible: 클라우드 리소스 프로비저닝 및 노드 초기 설정 자동화 스크립트 작성

<br/>

## 3. 기술 스택 (Tech Stack)

| 구분                  | 기술                                                        |
| --------------------- | ----------------------------------------------------------- |
| **Frontend** | Javascript, React, Next.js         |
| **Backend** | Java, Spring Boot, Spring Data JPA, Spring Security         |
| **Infrastructure** | Kubernetes (k0s), Docker, Nginx                           |
| **DevOps & CI/CD** | GitHub Actions, Flux CD, Terraform, Ansible               |
| **Database** | Oracle                                           |
| **Monitoring** | Prometheus, Grafana, Loki                        |

<br/>

## 4. 아키텍처 (Architecture)

<p align="center">
  <img src="img/cd.PNG" alt="배포 아키텍처" width="80%"/>
</p>
<p align="center">
  <img src="img/service.PNG" alt="시스템 아키텍처" width="80%"/>
</p>

### 시스템 구성 및 설계 의도
현재 아키텍처는 프론트엔드와 백엔드가 분리된 구조이며, 모두 단일 애플리케이션으로 구성된 모놀리식 아키텍처입니다. 빠른 개발 속도와 관리의 용이성을 위해 이 구조를 채택했습니다. 또한, 쿠버네티스 안에서 레플리카수를 2로 지정하여 단일 장애점을 방지하였습니다.

### 인프라 구성 및 자동화
클라우드 비용 문제로 인스턴스를 자주 재생성해야 하는 비효율이 발생했고, 이를 해결하고자 Terraform과 Ansible을 도입했습니다. Terraform으로 인프라를 코드로 관리(IaC)하고, Ansible로 노드의 초기 설정을 자동화하여 수동 작업의 반복을 줄여나가고 있습니다. 오케스트레이션 도구로는 설치가 간편하고 리소스 요구사항이 낮은 경량 Kubernetes 배포판인 k0s를 채택하여 낮은 성능과 학습 환경에 최적화했습니다.

### CI/CD 파이프라인 (GitOps)
CI/CD 도구로는 GitHub Actions를 사용했습니다. 소스 코드 저장소인 GitHub과의 연동성이 뛰어나고, 별도의 서버 구축 없이 YAML 파일로 손쉽게 워크플로우를 관리할 수 있습니다. 이 파이프라인을 통해 Docker 이미지가 빌드 및 푸시되고, 버전 정보가 변경되면 FluxCD를 통해 감지되어 클러스터에 적용됩니다.

<br/>

## 5. 트러블슈팅 및 학습 과정 (Troubleshooting)

#### Kubernetes(k0s) 클러스터 외부 접속 불가 문제
- k0s 클러스터 구축 후, Ingress를 통한 외부 접속이 불가한 문제를 해결했습니다.
- 원인은 클라우드 보안 정책(VCN/NSG)의 포트설정과 Ingress Controller의 모드설정 이었습니다. IngressController를 호스트 네트워크 모드로 설정하고 포트를 80,443으로 설정하여 로드밸런서 도입 전 임시적으로 문제를 해결했습니다.

#### Cert-Manager HTTPS 인증서 발급 실패 문제
- Cert-Manager의 TLS 인증서 자동 발급 실패 문제를 해결했습니다.
- 로그 분석 결과, Helm 설치 시 CRD(Custom Resource Definition)가 늦게 생성되는 문제와 Ingress의 정규식 경로 차단이라는 두 가지 원인을 발견했습니다. Helm 차트의 CRD 설치 옵션만으로는 해결이 되지않아, 미리 CRD를 설치할수 있도록 자동화 했습니다. 또한, Ingress의 정규식 설정을 변경하여 문제를 해결했습니다.

<br/>

## 6. 향후 개선 계획 (Future Plan)

#### 머신러닝 추천 시스템 도입:
  - 사용자 구매/조회 이력 데이터를 기반으로 한 상품 추천 모델 개발  
  - 모델 학습 및 배포, 서빙을 위한 MLOps 파이프라인 구축  

#### 모니터링 시스템 고도화:
  - 모니터링 시스템 세부 설정 및 알림 연동  

#### MSA 구조 도입:
  - API Gateway 도입 및 서비스 간 통신 안정성 확보  
