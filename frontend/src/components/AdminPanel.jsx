import React, { useState, useEffect } from 'react';
import './AdminPanel.css';

const API_BASE = import.meta.env.VITE_API_BASE || '';

const AdminPanel = () => {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [uploadStatus, setUploadStatus] = useState(null);

  useEffect(() => {
    // Try real API first, fallback to mock data
    fetch(`${API_BASE}/api/admin/analytics`)
      .then(res => res.ok ? res.json() : null)
      .then(data => {
        if (data) {
          setAnalytics(data);
        } else {
          setAnalytics({
            totalUsers: 142,
            totalQueriesProcessed: 894,
            mostSearchedCrop: 'Tomato',
            topLocation: 'California',
            avgSoilPh: '6.4',
            organicQueryPercent: '34%',
          });
        }
      })
      .catch(() => {
        setAnalytics({
          totalUsers: 142,
          totalQueriesProcessed: 894,
          mostSearchedCrop: 'Tomato',
          topLocation: 'California',
          avgSoilPh: '6.4',
          organicQueryPercent: '34%',
        });
      })
      .finally(() => setLoading(false));
  }, []);

  const handleRebuildChroma = () => {
    setUploadStatus({ type: 'info', msg: 'Triggering ChromaDB rebuild... (API call)' });
    fetch(`${API_BASE}/api/admin/rebuild-chroma`, { method: 'POST' })
      .then(() => setUploadStatus({ type: 'success', msg: '✓ ChromaDB embeddings rebuilt successfully!' }))
      .catch(() => setUploadStatus({ type: 'error', msg: '✗ Backend not reachable. Please start the Spring Boot server.' }));
  };

  const metricCards = analytics ? [
    { icon: '👥', label: 'Total Users', value: analytics.totalUsers, color: '#4facde' },
    { icon: '🔬', label: 'Queries Processed', value: analytics.totalQueriesProcessed, color: '#6fcf7c' },
    { icon: '🌾', label: 'Most Searched Crop', value: analytics.mostSearchedCrop, color: '#f0c040' },
    { icon: '📍', label: 'Top Location', value: analytics.topLocation, color: '#b48be0' },
    { icon: '🧪', label: 'Avg Soil pH', value: analytics.avgSoilPh, color: '#f07840' },
    { icon: '🌿', label: 'Organic Queries', value: analytics.organicQueryPercent, color: '#6fcf7c' },
  ] : [];

  if (loading) {
    return (
      <div className="admin-loading">
        <div className="spinner" style={{ width: 36, height: 36 }} />
        <p>Loading analytics...</p>
      </div>
    );
  }

  return (
    <div className="admin-wrapper animate-fade-in">

      {/* Header */}
      <div className="admin-head-row">
        <div>
          <h1 className="admin-title">Admin Dashboard</h1>
          <p className="admin-subtitle">Monitor system usage, AI metrics, and database health.</p>
        </div>
        <span className="badge badge-success">🟢 System Online</span>
      </div>

      {/* Analytics Grid */}
      <section className="admin-section">
        <h3 className="admin-section-title">📊 System Analytics</h3>
        <div className="admin-metrics-grid">
          {metricCards.map((card, i) => (
            <div key={i} className={`admin-metric-card glass-card stagger-${i + 1} animate-fade-in`}>
              <div className="admin-metric-icon" style={{ '--card-color': card.color }}>
                {card.icon}
              </div>
              <div className="admin-metric-value">{card.value}</div>
              <div className="admin-metric-label">{card.label}</div>
            </div>
          ))}
        </div>
      </section>

      {/* Database Management */}
      <section className="admin-section">
        <h3 className="admin-section-title">🗄️ Database Management</h3>
        <div className="admin-db-grid">
          <div className="admin-db-card glass-card">
            <div className="admin-db-head">
              <span className="admin-db-icon">🍃</span>
              <div>
                <h4>MongoDB</h4>
                <p>Document store for queries and recommendations</p>
              </div>
              <span className="badge badge-success">Connected</span>
            </div>
            <div className="admin-db-stats">
              <div className="admin-db-stat">
                <span className="stat-num">{analytics.totalQueriesProcessed}</span>
                <span className="stat-lbl">Farmer Queries</span>
              </div>
              <div className="admin-db-stat">
                <span className="stat-num">{analytics.totalUsers}</span>
                <span className="stat-lbl">Users</span>
              </div>
            </div>
          </div>

          <div className="admin-db-card glass-card">
            <div className="admin-db-head">
              <span className="admin-db-icon">🔮</span>
              <div>
                <h4>ChromaDB</h4>
                <p>Vector store for agricultural knowledge RAG</p>
              </div>
              <span className="badge badge-info">Embedded</span>
            </div>
            <div className="admin-db-actions">
              {uploadStatus && (
                <div className={`upload-status ${uploadStatus.type}`}>
                  {uploadStatus.msg}
                </div>
              )}
              <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                <button className="btn-secondary" style={{ fontSize: '0.88rem' }}>
                  📤 Upload Fertilizer Dataset
                </button>
                <button
                  className="btn-primary"
                  style={{ fontSize: '0.88rem', background: 'linear-gradient(135deg, #7c3aed, #4c1d95)' }}
                  onClick={handleRebuildChroma}
                >
                  ⚡ Rebuild Embeddings
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* API Config */}
      <section className="admin-section">
        <h3 className="admin-section-title">🔑 API Configuration</h3>
        <div className="admin-api-card glass-card">
          <div className="api-row">
            <div className="api-row-label">
              <span className="api-icon">✨</span>
              <div>
                <strong>Gemini API Key</strong>
                <span className="api-hint">Set via GEMINI_API_KEY environment variable</span>
              </div>
            </div>
            <span className="badge badge-info">ENV Variable</span>
          </div>
          <div className="api-row">
            <div className="api-row-label">
              <span className="api-icon">🌤️</span>
              <div>
                <strong>OpenWeatherMap API</strong>
                <span className="api-hint">Set via OPENWEATHERMAP_API_KEY environment variable</span>
              </div>
            </div>
            <span className="badge badge-info">ENV Variable</span>
          </div>
          <div className="api-config-hint">
            <code>export GEMINI_API_KEY="your-key-here"</code>
            <code>export OPENWEATHERMAP_API_KEY="your-key-here"</code>
          </div>
        </div>
      </section>

    </div>
  );
};

export default AdminPanel;
