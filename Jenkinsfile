pipeline {
    agent any

    parameters {
        string(name: 'PETCLINIC_REPO_URL', description: 'SSH URL of the petclinic-rest repository')
        string(name: 'PETCLINIC_BRANCH', defaultValue: 'main', description: 'Branch to checkout')
        string(name: 'PETCLINIC_CREDENTIALS_ID', description: 'Jenkins credentials ID for SSH access to the repository')
    }

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
