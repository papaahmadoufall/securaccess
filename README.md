# 🔐 SecurAccess Enterprise

[![Deploy to AWS EC2](https://github.com/YOUR_USERNAME/securaccess-enterprise/actions/workflows/deploy.yml/badge.svg)](https://github.com/YOUR_USERNAME/securaccess-enterprise/actions/workflows/deploy.yml)
[![Test Suite](https://github.com/YOUR_USERNAME/securaccess-enterprise/actions/workflows/test.yml/badge.svg)](https://github.com/YOUR_USERNAME/securaccess-enterprise/actions/workflows/test.yml)

A comprehensive access management system built with Spring Boot and Vue.js for enterprise security and access control.

## 🌐 Live Application

- **Frontend**: http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com
- **API**: http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/api
- **H2 Console**: http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/api/h2-console

## 🚀 Features

### 👥 User Management
- **Multi-role authentication** (Worker, Host, Manager)
- **CRUD operations** for all user types
- **Department-based organization**
- **Active/inactive status management**

### 🔑 Access Control
- **QR code generation** for secure access
- **Access logging and history**
- **Real-time access tracking**
- **Zone-based access control**

### 📊 Analytics & Reporting
- **Dashboard with statistics**
- **Access history reports**
- **Department analytics**
- **Event management and tracking**

### 🛡️ Security
- **JWT token authentication**
- **Role-based access control**
- **CORS configuration**
- **Input validation and sanitization**

## 🏗️ Architecture

### Backend (Spring Boot 3.2.1)
- **Spring Security** for authentication and authorization
- **Spring Data JPA** with Hibernate ORM
- **H2 Database** for development (easily configurable for PostgreSQL/MySQL)
- **REST API** with comprehensive endpoints
- **ZXing** for QR code generation
- **Maven** for dependency management

### Frontend (Vue.js 3)
- **Vue 3** with Composition API
- **Vite** for fast development and building
- **Tailwind CSS** for styling
- **Vue Router** for navigation
- **Heroicons** for UI icons

### Infrastructure
- **AWS EC2** deployment
- **Nginx** reverse proxy
- **GitHub Actions** CI/CD
- **Automated testing and deployment**

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Backend Framework | Spring Boot | 3.2.1 |
| Frontend Framework | Vue.js | 3.4.21 |
| Database | H2 (dev) / PostgreSQL (prod) | Latest |
| Build Tool (Backend) | Maven | 3.9.6 |
| Build Tool (Frontend) | Vite | 4.5.14 |
| Java Version | Amazon Corretto | 17 |
| Node.js | Node.js | 18.20.8 |
| Styling | Tailwind CSS | 3.4.3 |
| Web Server | Nginx | 1.28.0 |
| Cloud Platform | AWS EC2 | Free Tier |

## Fonctionnalités Principales

- 🔐 **Authentification sécurisée** avec JWT
- 👥 **Gestion des utilisateurs** et participants
- 📅 **Gestion des événements** avec zones d'accès
- 🔲 **Génération automatique de QR codes**
- 📱 **Scanner QR codes** en temps réel
- 📊 **Tableaux de bord** et rapports détaillés
- 🌓 **Mode sombre/clair**

## Structure du Projet

```
├── backend/                 # Projet Java EE
│   ├── src/main/java/com/securaccess/enterprise/
│   │   ├── entities/       # Classes JPA
│   │   ├── repositories/   # Accès aux données
│   │   ├── services/       # Logique métier
│   │   ├── resources/      # API REST
│   │   ├── security/       # JWT et authentification
│   │   └── utils/          # Utilitaires (QR codes)
│   ├── src/main/resources/
│   │   └── META-INF/persistence.xml
│   └── pom.xml
│
├── frontend/               # Projet Nuxt.js
│   ├── pages/             # Routes de l'application
│   ├── components/        # Composants Vue
│   ├── layouts/           # Mises en page
│   ├── composables/       # Fonctions réutilisables
│   ├── assets/css/        # Styles CSS
│   └── nuxt.config.ts
│
└── README.md
```

## Installation et Configuration

### Prérequis
- Java 17+
- Node.js 18+
- MySQL Server (port 3308)
- Maven 3.6+
- Serveur d'applications (WildFly/Payara)

### Base de Données
- **Port**: 3308
- **Utilisateur**: root
- **Mot de passe**: (vide)
- **Base de données**: securaccess_db

### Backend
```bash
cd backend
mvn clean install
# Déployer le WAR sur votre serveur d'applications
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## API Configuration

L'API backend sera accessible sur : `http://localhost:8080/securaccess-enterprise/api`

## Développement

Le projet utilise une architecture séparée avec un backend Java EE servant une API REST et un frontend Nuxt.js consommant cette API.

## Sécurité

- Authentification JWT
- Validation des QR codes en temps réel
- Gestion fine des rôles et permissions
- Protection contre les accès non autorisés

## Licence

Ce projet est développé pour un usage entreprise.Testing GitHub Actions deployment...
Deploying QR code improvements to production...
