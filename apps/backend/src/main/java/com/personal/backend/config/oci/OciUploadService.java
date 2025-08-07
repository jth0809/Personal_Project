package com.personal.backend.config.oci;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.personal.backend.dto.ImageDto;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Profile("!test")
public class OciUploadService {

    private final ObjectStorage objectStorageClient;

    private final OciProperties ociProperties;

    public ImageDto.GenerateUploadUrlResponse generatePreAuthenticatedUploadUrl(ImageDto.GenerateUploadUrlRequest request) {
        // 1. OCI에 저장될 파일 이름 생성 (중복 방지를 위해 UUID 사용)
        String uniqueObjectName = UUID.randomUUID() + "_" + request.fileName();

        // 2. 사전 인증된 요청(PAR) 생성 정보 설정
        CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                .name("par-for-" + uniqueObjectName) // PAR 자체의 이름
                .objectName(uniqueObjectName) // 버킷에 저장될 객체(파일) 이름
                .accessType(CreatePreauthenticatedRequestDetails.AccessType.ObjectWrite) // 쓰기(업로드) 전용 권한
                .timeExpires(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES))) // 10분 후 만료
                .build();

        CreatePreauthenticatedRequestRequest parRequest = CreatePreauthenticatedRequestRequest.builder()
                .namespaceName(ociProperties.namespace())
                .bucketName(ociProperties.bucketName())
                .createPreauthenticatedRequestDetails(details)
                .build();

        // 3. OCI SDK를 통해 PAR 생성 요청
        CreatePreauthenticatedRequestResponse response = objectStorageClient.createPreauthenticatedRequest(parRequest);

        // 4. 응답에서 필요한 URL 추출 및 조합
        String fullPath = response.getPreauthenticatedRequest().getAccessUri();
        String ociHost = objectStorageClient.getEndpoint().replace("/20160918", "");
        
        String uploadUrl = ociHost + fullPath;
        String imageUrl = ociHost + "/n/" + ociProperties.namespace() + "/b/" + ociProperties.bucketName() + "/o/" + uniqueObjectName;

        return new ImageDto.GenerateUploadUrlResponse(uploadUrl, imageUrl);
    }
}