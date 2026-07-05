import { useState } from 'react'
import './App.css'
import LandingPage from './components/LandingPage'
import Dashboard from './components/Dashboard'
import AdminPanel from './components/AdminPanel'

function App() {
  const [currentPage, setCurrentPage] = useState('landing')

  return (
    <div className="app-container">
      <nav className="navbar">
        <div className="logo">
          <span className="logo-icon">🌱</span>
          AgriAgent
        </div>
        <div className="nav-links">
          <button
            className={`nav-btn ${currentPage === 'landing' ? 'active' : ''}`}
            onClick={() => setCurrentPage('landing')}
          >
            Home
          </button>
          <button
            className={`nav-btn ${currentPage === 'dashboard' ? 'active' : ''}`}
            onClick={() => setCurrentPage('dashboard')}
          >
            Dashboard
          </button>
          <button
            className={`nav-btn ${currentPage === 'admin' ? 'active' : ''}`}
            onClick={() => setCurrentPage('admin')}
          >
            Admin
          </button>
        </div>
      </nav>

      <main className="main-content">
        {currentPage === 'landing' && <LandingPage onGetStarted={() => setCurrentPage('dashboard')} />}
        {currentPage === 'dashboard' && <Dashboard />}
        {currentPage === 'admin' && <AdminPanel />}
      </main>

      <footer className="footer">
        <span>© 2026 AgriAgent · Fertilizer Recommendation System</span>
        <div className="footer-links">
          <a href="#">Privacy</a>
          <a href="#">Terms</a>
          <a href="#">Contact</a>
        </div>
      </footer>
    </div>
  )
}

export default App
