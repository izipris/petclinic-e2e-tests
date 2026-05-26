pipeline {
    agent any

    stages {
        stage('Checkout petclinic-rest') {
            steps {
                dir('spring-petclinic-rest') {
                    git(
                        url: params.PETCLINIC_REPO_URL,
                        branch: params.PETCLINIC_BRANCH,
                        credentialsId: params.PETCLINIC_CREDENTIALS_ID
                    )
                }
            }
        }

        stage('Start services') {
            steps {
                sh './pet-clinic-rest.sh up'
                sh '''
                    echo "Waiting for petclinic-rest to be ready..."
                    for i in $(seq 1 60); do
                      if curl -sf http://docker:9966/petclinic/actuator/health > /dev/null 2>&1; then
                        echo "petclinic-rest is ready"
                        exit 0
                      fi
                      sleep 2
                    done
                    echo "petclinic-rest failed to become ready after 120s"
                    docker logs petclinic-app || true
                    exit 1
                '''
            }
        }

        stage('Run tests') {
            steps {
                sh './mvnw test -Dspring.profiles.active=ManagedDocker'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/**/*.xml'
            sh './pet-clinic-rest.sh down'
        }
    }
}
