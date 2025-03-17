## 도커 컴포즈 명령어

```yaml

docker-compose down -v # 컨테이너 종료 및 볼륨 삭제

docker-compose up -d # 백그라운드 실행 

docker-compose ps # 컨테이너 상태 확인

docker-compose logs -f # 로그 확인
```

## docker-compose.yaml 

- image : 도커 이미지 이름 (가장 중요)

- container_name : 컨테이너 이름

- ports : 포트 매핑

- networks : 네트워크 설정 (컨테이너 간 통신) : elastic 네트워크 생성

- environment : 환경변수 설정

- depends_on : 의존성 설정 (컨테이너 실행 순서) : 여기서는 엘라스틱서치가 먼저 실행되어야 키바나가 실행됨

```yaml
    networks:
        elastic:
        driver: bridge 
```
네트워크 설정을 통해 컨테이너 간 통신을 가능하게 해줍니다.
