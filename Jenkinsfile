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
                docker {
                    image 'maven:3-alpine'
                    args '-v maven-repo:/root/.m2'
                }
            }
            steps { 
                sh 'pwd && ls -alh'
                sh 'mvn -v'
                sh 'mvn clean package -s "/var/jenkins_home/appconfig/maven/settings.xml" -Dmaven.test.skip=true'
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