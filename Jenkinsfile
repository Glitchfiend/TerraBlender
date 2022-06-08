@Library('forge-shared-library')_

pipeline {
    agent {
        docker {
            image 'gradle:7-jdk17'
        }
    }
    environment {
        GRADLE_ARGS = '-Dorg.gradle.daemon.idletimeout=5000'
    }

    stages {
        stage('fetch') {
            steps {
                checkout scm
            }
        }
        stage('build') {
            steps {
                withGradle {
                    sh './gradlew ${GRADLE_ARGS} --refresh-dependencies'
                }
                script {
                    env.MYVERSION = sh(returnStdout: true, script: './gradlew :properties -q | grep "^version:" | awk \'{print $2}\'').trim()
                }
            }
            post {
                success {
                    writeChangelog(currentBuild, "build/TerraBlender-${env.MYVERSION}-changelog.txt")
                }
            }
        }
        stage('publish') {
            when {
                not {
                    changeRequest()
                }
            }
            environment {
                CURSE_API_KEY = credentials('curse-api-key')
            }
            steps {
                withGradle {
                    sh 'rm -rf ./Forge/build/classes' // Ensure refmaps are generated/included
                    sh './gradlew ${GRADLE_ARGS} curseforge -PcurseApiKey=${CURSE_API_KEY}'
                }
                withCredentials([usernamePassword(credentialsId: 'maven-adubbz-user', usernameVariable: 'MAVEN_USER', passwordVariable: 'MAVEN_PASSWORD')]) {
                    sh 'rm -rf ./Forge/build/classes' // Ensure refmaps are generated/included
                    sh './gradlew ${GRADLE_ARGS} publish'
                }
            }
        }
    }
}