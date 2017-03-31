#!groovy

pipeline {
    tools {
        maven 'Maven 3'
    }
    agent { label "devel8-head" }
    stages {
        stage("Checkout") {
            steps {
                checkout scm
            }
        }
        stage("build") {
            steps {
                echo "build Missing"
            }
        }
        stage("Repo Upload") {
            steps {
                echo "Repo Upload Missing"
            }
        }
    }
}