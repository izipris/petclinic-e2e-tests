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
                sh 'PETCLINIC_URL=http://docker:9966/petclinic ./pet-clinic-rest.sh up'
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
