pipeline { 
    agent any 

    environment {
        GLOBAL_WS = "${WORKSPACE}"
    }

    stages {
        stage('Check') { 
            steps { 
                echo "Check"
                sh 'printenv'
                sh 'java -version'
                sh 'git --version'
                sh 'docker version'
                sh 'pwd && ls -alh'
            }
        }        
        stage('Compile') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    args '-v maven-repo:/root/.m2'
                }
            }
            steps { 
                echo "Compile"
                sh 'pwd && ls -alh'
                sh 'mvn -v'
                sh 'echo ${WORKSPACE}'
                sh 'echo ${GLOBAL_WS}'
                sh 'cd ${GLOBAL_WS} && mvn clean package -s "/var/jenkins_home/appconfig/maven/settings.xml" -Dmaven.test.skip=true'
            }
        }
        stage('Test'){
            steps {
                echo "Test"
            }
        }
        stage('Build') {
            steps {
                echo "Build"
                sh 'docker version'
                sh 'pwd && ls -alh'
                sh 'docker build -t java-devops-demo .'
            }
        }
        stage('Push') {
             input {
                 message "Push to Alicloud"
                 ok "Yes"
                 parameters {
                     string(name: 'APP_VER', defaultValue: 'v1.0', description: 'Image Version')
                     choice choices: ['AWS', 'Azure', 'AlibabaCloud', 'GoogleCloud'], description: 'Image repo', name: 'DEPLOY_WHERE'
                 }
             }
            steps {
                echo "Push Image"

            }
        }
        stage('Depoloy-Staging') {
            steps {
                echo "Deploy Artifact"
                sh 'docker rm -f java-devops-demo'
                sh 'docker run -d -p 8888:8080 --name java-devops-demo java-devops-demo'
            }
        }
        stage('Report') {
            steps {
                echo "Report REST API Send Mail"
            }
        }
        stage('Depoloy-Prod') {
            steps {
                echo "Report REST API Send Mail"
            }
        }
    }
    post {
        failure {
            echo "failure.... $currentBuild.result"
        }
        success {
            echo "success.... $currentBuild.result"
        }
    }
}