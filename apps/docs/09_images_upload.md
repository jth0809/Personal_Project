# 09_images_upload.md

## 목적

상품 이미지 업로드/삭제 플로우 정의(OCI Object Storage 연계).

## 백엔드 API (컨트롤러 일부)

- Base: `/images` (컨트롤러 실제 경로명은 `ImageController` 참고)
- 업로드: `POST /images/products/{productId}` (폼데이터/파일)
- 삭제: `DELETE /images/{objectName}/products/{productId}`

> 컨트롤러 일부에 문자열 리터럴 타이포(`"\"/{objectName}/products/{productId}\""`)가 보여 수정 필요. 운영 전 경로 확정 필수.

## 프론트 구현

- 관리자 전용 업로드 UI
- `FormData` 전송(멀티파트)
- 업로드 성공 시 상품 상세 이미지 갤러리 갱신

## 완료 기준

- 큰 용량/네트워크 오류 처리
- 썸네일/원본 표시 정책 합의
