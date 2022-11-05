pipeline { 
    agent any 

    environment {
    hello = "rai"
    world = "123456"
    }


    stages {
        stage('Build') { 
            steps { 
                echo "Build"
                echo "$hello"
                echo "$world"
                sh 'pwd && ls -alh'
                sh 'printenv'
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