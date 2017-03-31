#!groovy

pipeline {
    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
        timeout(time: 1, unit: 'HOURS') 
    }
    triggers {
        pollSCM('H/5 * * * *')
    }

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
                    junit 'target/failsafe-reports/*.xml'

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
            emailext subject: "SUCCESSFUL: Job '${env.JOB_NAME}' [${env.BUILD_NUMBER}]",
                    body: """"SUCCESSFUL: Job '${env.JOB_NAME}' checkout console output at ${env.BUILD_URL}""",
                    to: 'jp@dbc.dk'
            sh "mvn deploy"
        }
        failure {
            emailext subject: "FAILED: Job '${env.JOB_NAME}' [${env.BUILD_NUMBER}]",
                    body: """"FAILED: Job '${env.JOB_NAME}' checkout console output at ${env.BUILD_URL}""",
                    to: 'jp@dbc.dk',
                    attachLog: true
        }
    }
}