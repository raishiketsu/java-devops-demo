pipeline { 
    agent any 

    environment {
    hello = "rai"
    world = "123456"
    }


    stages {
        stage('Check') { 
            steps { 
                sh 'pwd && ls -alh'
                sh 'printenv'
                sh 'java -version'
                sh 'git --version'
                sh 'docker version'
            }
        }        
        stage('Build') { 
            steps { 
                echo "Build"
                echo "$hello"
                echo "$world"
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