#!groovy

pipeline {
    tools {
        maven 'Maven 3'
    }
    agent { label "devel8-head" }
    stages {
        stage("build") {
            steps {
                sh "mvn verify pmd:pmd findbugs:findbugs"
            }
            post {
                always {
                    junit '**/target/*.xml'

                }
            }
        }
    }
    post {
        always {
            archiveArtifacts '**/target/*.war, **/target/*.jar'
        }
        success {
            build job: 'hive', wait: false
            emailext subject: "SUCCESSFUL: Job '${env.JOB_NAME}' [${env.BUILD_NUMBER}",
                    body: """"SUCCESSFUL: Job '${env.JOB_NAME}' checkout console output at ${env.BUILD_URL}""",
                    to: 'jp@dbc.dk'
        }
    }
}