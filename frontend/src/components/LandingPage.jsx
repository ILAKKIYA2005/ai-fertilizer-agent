import React, { useEffect, useRef, useState } from 'react';
import './LandingPage.css';

const stats = [
  { value: '94%', label: 'Accuracy Rate', icon: '🎯' },
  { value: '12K+', label: 'Farmers Helped', icon: '👨‍🌾' },
  { value: '38', label: 'Crops Supported', icon: '🌾' },
  { value: '2.4×', label: 'Yield Improvement', icon: '📈' },
];

const features = [
  {
    icon: '🌍',
    title: 'Location Aware',
    desc: 'Integrates with live weather data to adjust recommendations dynamically based on real-time rainfall, temperature, and humidity.',
    color: '#4facde',
  },
  {
    icon: '🧪',
    title: 'Deep Soil Analysis',
    desc: 'Precisely analyzes NPK levels, pH balance, and soil moisture to prescribe the exact nutrients your crops are deficient in.',
    color: '#6fcf7c',
  },
  {
    icon: '🤖',
    title: 'AI-Powered RAG',
    desc: 'Powered by Google Gemini and ChromaDB vector search to deliver explainable, science-backed recommendations in seconds.',
    color: '#f0c040',
  },
  {
    icon: '🌿',
    title: 'Organic Options',
    desc: 'Every recommendation includes eco-friendly organic alternatives for sustainable, chemical-free farming practices.',
    color: '#b48be0',
  },
  {
    icon: '📊',
    title: 'Full Report PDF',
    desc: 'Download a complete, printable PDF report of the AI recommendation, timings, precautions, and expected yield improvement.',
    color: '#f07840',
  },
  {
    icon: '🔒',
    title: 'Secure & Private',
    desc: 'Your farm data stays private. All queries are encrypted and stored securely in our MongoDB database.',
    color: '#40bff0',
  },
];

const steps = [
  { num: '01', title: 'Enter Crop Details', desc: 'Fill in your crop type, location, soil NPK levels, pH, and current growth stage.' },
  { num: '02', title: 'AI Analyzes Context', desc: 'The RAG engine searches our agricultural knowledge base and builds a detailed prompt for Gemini.' },
  { num: '03', title: 'Get Your Report', desc: 'Receive a structured, explainable recommendation with quantities, timing, and precautions.' },
];

const demoSteps = [
  {
    icon: '🌱',
    title: 'Enter Your Farm Details',
    desc: 'Fill in your crop type, soil pH, location, and describe the problem you are facing with your field.',
    color: '#6fcf7c',
  },
  {
    icon: '🤖',
    title: 'AI Analyzes Your Data',
    desc: 'Our Groq-powered LLaMA engine cross-references your inputs with a scientific knowledge base of fertilizer data.',
    color: '#4facde',
  },
  {
    icon: '📋',
    title: 'Get a Precise Recommendation',
    desc: 'Receive the exact fertilizer, dosage, application method, timing, and irrigation advice — all tailored to your field.',
    color: '#f0c040',
  },
  {
    icon: '📥',
    title: 'Download Your Report',
    desc: 'Export a full PDF report with all recommendations and share it with your team or agronomist instantly.',
    color: '#f2994a',
  },
];

const LandingPage = ({ onGetStarted }) => {
  const particlesRef = useRef(null);
  const [showDemo, setShowDemo] = useState(false);
  const [demoStep, setDemoStep] = useState(0);

  useEffect(() => {
    // Simple particle animation on canvas
    const canvas = particlesRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;

    const particles = Array.from({ length: 40 }, () => ({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      r: Math.random() * 2 + 0.5,
      vx: (Math.random() - 0.5) * 0.3,
      vy: (Math.random() - 0.5) * 0.3,
      alpha: Math.random() * 0.5 + 0.1,
    }));

    let animId;
    const draw = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      particles.forEach(p => {
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(111, 207, 124, ${p.alpha})`;
        ctx.fill();
        p.x += p.vx;
        p.y += p.vy;
        if (p.x < 0 || p.x > canvas.width) p.vx *= -1;
        if (p.y < 0 || p.y > canvas.height) p.vy *= -1;
      });
      animId = requestAnimationFrame(draw);
    };
    draw();
    return () => cancelAnimationFrame(animId);
  }, []);

  return (
    <div className="landing-wrapper">

      {/* ---- HERO ---- */}
      <section className="hero-section">
        <canvas ref={particlesRef} className="hero-particles" />

        <div className="hero-badge animate-fade-in stagger-1">
          <span className="badge badge-success">🚀 AI-Powered · v2.0</span>
        </div>

        <h1 className="hero-title animate-fade-in stagger-2">
          Smarter Farming with
          <span className="hero-gradient-text"> AI-Driven</span>
          <br />Fertilizer Intelligence
        </h1>

        <p className="hero-subtitle animate-fade-in stagger-3">
          Stop guessing. Let our RAG-powered engine analyze your soil, weather, and crop stage
          to generate precise, science-backed fertilizer recommendations in seconds.
        </p>

        <div className="hero-actions animate-fade-in stagger-4">
          <button className="btn-primary hero-cta-btn" onClick={onGetStarted}>
            🌱 Get My Recommendation
          </button>
          <button className="btn-secondary hero-demo-btn" onClick={() => { setDemoStep(0); setShowDemo(true); }}>
            ▶ Watch Demo
          </button>
        </div>

        <div className="hero-trust animate-fade-in stagger-5">
          <span className="trust-item">✓ No signup needed</span>
          <span className="trust-sep">·</span>
          <span className="trust-item">✓ Instant results</span>
          <span className="trust-sep">·</span>
          <span className="trust-item">✓ 100% free to try</span>
        </div>
      </section>

      {/* ---- STATS ---- */}
      <section className="stats-section animate-fade-in">
        {stats.map((s, i) => (
          <div key={i} className={`stat-card glass-card stagger-${i + 1} animate-fade-in`}>
            <div className="stat-icon">{s.icon}</div>
            <div className="stat-value">{s.value}</div>
            <div className="stat-label">{s.label}</div>
          </div>
        ))}
      </section>

      {/* ---- HOW IT WORKS ---- */}
      <section className="section-block animate-fade-in">
        <div className="section-header">
          <span className="badge badge-info">Simple Process</span>
          <h2 className="section-title">How It Works</h2>
          <p className="section-subtitle">Get precise recommendations in under 30 seconds</p>
        </div>
        <div className="steps-grid">
          {steps.map((step, i) => (
            <div key={i} className={`step-card glass-card animate-fade-in stagger-${i + 1}`}>
              <div className="step-num">{step.num}</div>
              <h3 className="step-title">{step.title}</h3>
              <p className="step-desc">{step.desc}</p>
              {i < steps.length - 1 && <div className="step-arrow">→</div>}
            </div>
          ))}
        </div>
      </section>

      {/* ---- FEATURES ---- */}
      <section className="section-block animate-fade-in">
        <div className="section-header">
          <span className="badge badge-success">Features</span>
          <h2 className="section-title">Everything You Need</h2>
          <p className="section-subtitle">Built for modern precision agriculture</p>
        </div>
        <div className="features-grid">
          {features.map((f, i) => (
            <div key={i} className={`feature-card glass-card animate-fade-in stagger-${(i % 3) + 1}`}>
              <div className="feature-icon-wrap" style={{ '--feature-color': f.color }}>
                <span className="feature-icon">{f.icon}</span>
              </div>
              <h3 className="feature-title">{f.title}</h3>
              <p className="feature-desc">{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* ---- CTA BAND ---- */}
      <section className="cta-band glass-card animate-fade-in">
        <div className="cta-inner">
          <h2 className="cta-title">Ready to boost your harvest?</h2>
          <p className="cta-sub">Join thousands of farmers already using AI to maximize crop yield.</p>
          <button className="btn-primary cta-btn" onClick={onGetStarted}>
            Start Now — It's Free →
          </button>
        </div>
        <div className="cta-deco">🌾</div>
      </section>

      {/* ---- DEMO MODAL ---- */}
      {showDemo && (
        <div className="demo-overlay" onClick={() => setShowDemo(false)}>
          <div className="demo-modal" onClick={e => e.stopPropagation()}>
            <button className="demo-close" onClick={() => setShowDemo(false)}>✕</button>

            <div className="demo-header">
              <span className="demo-tag">▶ How It Works</span>
              <h2 className="demo-title">See AgriAI in Action</h2>
            </div>

            <div className="demo-step-display">
              <div className="demo-step-icon" style={{ background: `${demoSteps[demoStep].color}22`, border: `2px solid ${demoSteps[demoStep].color}` }}>
                <span style={{ fontSize: '3rem' }}>{demoSteps[demoStep].icon}</span>
              </div>
              <div className="demo-step-number">Step {demoStep + 1} of {demoSteps.length}</div>
              <h3 className="demo-step-title" style={{ color: demoSteps[demoStep].color }}>
                {demoSteps[demoStep].title}
              </h3>
              <p className="demo-step-desc">{demoSteps[demoStep].desc}</p>
            </div>

            {/* Progress dots */}
            <div className="demo-dots">
              {demoSteps.map((_, i) => (
                <button
                  key={i}
                  className={`demo-dot ${i === demoStep ? 'active' : ''}`}
                  style={i === demoStep ? { background: demoSteps[demoStep].color } : {}}
                  onClick={() => setDemoStep(i)}
                />
              ))}
            </div>

            {/* Navigation */}
            <div className="demo-nav">
              <button
                className="demo-nav-btn"
                onClick={() => setDemoStep(s => Math.max(0, s - 1))}
                disabled={demoStep === 0}
              >
                ← Previous
              </button>
              {demoStep < demoSteps.length - 1 ? (
                <button
                  className="demo-nav-btn demo-nav-next"
                  onClick={() => setDemoStep(s => s + 1)}
                >
                  Next →
                </button>
              ) : (
                <button
                  className="demo-nav-btn demo-nav-next"
                  onClick={() => { setShowDemo(false); onGetStarted(); }}
                >
                  🌱 Try It Now →
                </button>
              )}
            </div>
          </div>
        </div>
      )}

    </div>
  );
};

export default LandingPage;
