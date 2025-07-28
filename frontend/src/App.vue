<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-container">
        <h1 class="nav-title">
          🔐 SecurAccess Enterprise
        </h1>
      </div>
    </nav>
    
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f8fafc;
  line-height: 1.6;
}

.navbar {
  background: white;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid #e5e7eb;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  height: 4rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #1f2937;
}

.main-content {
  min-height: calc(100vh - 4rem);
}
</style>