pipeline {
    agent any

    stages {

        stage('Build') {
            steps {
                sh '''
                echo "spring server 빌드 시작"

                chmod +x gradlew
                ./gradlew clean build -x test

                echo "spring server 빌드 완료"
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                echo "spring server 배포 시작"

                # 이동
                cd /home/ubuntu/app/spring-upload

                # 기존 jar 백업 (없어도 에러 안나게)
                cp pos_back_new-0.0.1-SNAPSHOT.jar pos_back_new-0.0.1-SNAPSHOT.jar.bak || true

                # 최신 jar 복사
                cp $WORKSPACE/build/libs/pos_back_new-0.0.1-SNAPSHOT.jar .

                # 파일 확인 (디버깅용)
                ls -al
                
                # 권한 설정 필요하면 리눅스 서버에서 실행
                #sudo chown ubuntu:ubuntu /home/ubuntu/app/spring-upload/pos_back_new-0.0.1-SNAPSHOT.jar

                # 컨테이너 재시작
                docker restart spring-pos

                echo "spring server 배포 완료"
                '''
            }
        }
    }
}