pipeline {
  agent any

  environment {
    // Remplace "tonuser" par ton compte Docker Hub
    DOCKER_IMAGE = 'docker.io/bayesaermbow/java17-render-app'
  }

  options { timestamps(); ansiColor('xterm') }
//ssggsgsgsgd
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Set Version') {
      steps {
        script {
          // VERSION = date + short SHA
          env.VERSION = sh(script: "echo $(date +%Y%m%d-%H%M%S)-$(git rev-parse --short HEAD)", returnStdout: true).trim()
          echo "Version: ${env.VERSION}"
        }
      }
    }

    stage('Build & Test (Maven)') {
      steps {
        sh 'mvn -B -Dmaven.test.failure.ignore=false clean package'
      }
    }

    stage('Docker Build') {
      steps {
        sh """
          docker build -t ${DOCKER_IMAGE}:${VERSION} -t ${DOCKER_IMAGE}:latest .
        """
      }
    }
//sdsddsddzz
    stage('Docker Login & Push') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh """
            echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin
            docker push ${DOCKER_IMAGE}:${VERSION}
            docker push ${DOCKER_IMAGE}:latest
            docker logout
          """
        }
      }
    }
//sgssggss
    stage('Deploy on Render') {
      steps {
        withCredentials([string(credentialsId: 'render-deploy-hook', variable: 'RENDER_DEPLOY_HOOK')]) {
          sh """
            echo "Triggering Render deploy..."
            curl -fsSL -X POST "\$RENDER_DEPLOY_HOOK" || (sleep 5 && curl -fsSL -X POST "\$RENDER_DEPLOY_HOOK")
          """
        }
      }
    }
  }

  post {
    success { echo "✅ OK — Image: ${DOCKER_IMAGE}:${VERSION} — Deploy Render déclenché." }
    failure { echo "❌ Échec du pipeline." }
  }
}
