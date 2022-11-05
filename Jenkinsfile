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