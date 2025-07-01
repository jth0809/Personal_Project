쇼핑몰 주요 페이지:

- 홈페이지 (상품 목록)
- 상품 상세 페이지
- 장바구니
- 결제 페이지
- 사용자 계정 (로그인/회원가입)
- 주문 내역
- 관리자 페이지 (상품 관리)

1. 상품 샘플 데이터 분리 ✅
   apps/frontend/src/contents/sampleProducts.ts 파일 생성
   6개의 공통 상품 데이터와 3개의 특별 상품 데이터로 분리
   모든 섹션에서 동일한 상품 데이터 공유
2. ProductsSection 수정 완료 ✅
   JSX 구문 에러 수정
   framer-motion의 AnimatePresence를 활용한 필터링 애니메이션
   상품 카드 호버 효과와 버튼 인터랙션
   새로운 샘플 데이터 사용
3. 모든 섹션에 framer-motion 적용 완료 ✅
   HeroSection: ✅ (이미 완료)
   플로팅 아이콘 애니메이션
   스크롤 인디케이터 효과
   CategoriesSection: ✅ (이미 완료)
   카드 호버 스케일 효과
   순차적 등장 애니메이션
   FeaturedProducts: ✅
   새 샘플 데이터로 교체
   복잡한 카드 호버 인터랙션
   오버레이 액션 버튼 애니메이션
   ProductsSection: ✅
   필터링 전환 애니메이션
   상품 카드 스케일 효과
   활성 필터 버튼 layoutId 애니메이션
   BrandStorySection: ✅
   백그라운드 부드러운 펄스 애니메이션
   플로팅 통계 카드 회전 효과
   리스트 아이템 호버 상호작용
   TestimonialsSection: ✅
   캐러셀 슬라이드 전환 애니메이션
   별점 순차 등장 효과
   통계 텍스트 글로우 애니메이션
   NewsletterSection: ✅
   구독 완료 시 confetti 효과
   폼 상호작용 애니메이션
   AnimatePresence를 활용한 상태 전환
4. 기술적 개선사항
   ✅ 모든 linter 에러 수정
   ✅ TypeScript 타입 안전성 확보
   ✅ 일관된 애니메이션 패턴 적용
   ✅ 성능 최적화된 애니메이션
   ✅ 접근성 친화적 인터랙션
