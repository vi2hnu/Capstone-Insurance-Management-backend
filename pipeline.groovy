pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/vi2hnu/Capstone-Insurance-Management-backend.git', branch: 'main'
            }
        }

        stage('Package Services') {
            steps {
                sh 'mvn clean package'
            }
            post {
                success {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts '**/target/*.jar'
                }
            }
        }

        stage('Stop Running Containers') {
            steps {
                sh 'docker compose down'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker compose build'
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker compose up -d'
            }
            post {
                success {
                    sh 'docker ps'
                }
            }
        }
    }
}
