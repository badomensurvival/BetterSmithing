pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                script {
                    // Configure Maven
                    env.MAVEN_HOME = tool 'Maven'
                    env.PATH = "${env.MAVEN_HOME}/bin:${env.PATH}"

                    // Run Maven command
                    sh 'mvn -B -DskipTests clean package'
                }
            }
        }
    }
}
