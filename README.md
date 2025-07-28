# SecurAccess Enterprise

Application de Gestion des Accès Temporaires et Suivi Entrées/Sorties

## Description

SecurAccess Enterprise est une application web et mobile permettant de gérer les accès temporaires via QR codes pour des événements ponctuels (réunions, journées portes ouvertes, prestataires) et de suivre les entrées/sorties quotidiennes du personnel et des visiteurs.

## Stack Technique

### Backend
- **Java EE (Jakarta EE)** - API RESTful avec JAX-RS
- **JPA/Hibernate** - Persistance des données
- **MySQL** - Base de données (port 3308)
- **JWT** - Authentification et autorisation
- **ZXing** - Génération et validation des QR codes
- **Maven** - Gestion des dépendances

### Frontend
- **Nuxt.js (Vue 3)** - Framework frontend
- **Tailwind CSS** - Framework CSS utilitaire
- **Shadcn UI** - Composants UI accessibles
- **Axios** - Client HTTP pour les API

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

Ce projet est développé pour un usage entreprise.