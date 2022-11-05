pipeline { 
    agent any 

    environment {
        hello = "rai"
        world = "123456"
    }

    stages {
        stage('Check') { 
            steps { 
                sh 'printenv'
                sh 'java -version'
                sh 'git --version'
                sh 'docker version'
                sh 'pwd && ls -alh'
            }
        }        
        stage('Build') {
            agent {
                docker 'maven:3-alpine'
            }
            steps { 
                sh 'pwd && ls -alh'
                sh 'mvn -v'
            }
        }
        stage('Test'){
            steps {
                echo "Test"
            }
        }
        stage('Deploy') {
            steps {
                echo "Deploy"
            }
        }
    }
}