import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield, Mail, Lock } from 'lucide-react';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const data = await login(email, password);
      navigate(data.role === 'ADMIN' ? '/admin' : '/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    }
    setLoading(false);
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%)'
    }}>
      <div style={{
        background: 'rgba(255,255,255,0.05)', backdropFilter: 'blur(10px)',
        borderRadius: '16px', padding: '2.5rem', width: '100%', maxWidth: '420px',
        border: '1px solid rgba(255,255,255,0.1)', boxShadow: '0 8px 32px rgba(0,0,0,0.3)'
      }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <Shield size={48} color="#4ade80" />
          <h1 style={{ color: '#fff', margin: '0.5rem 0 0.25rem' }}>EarnSafe</h1>
          <p style={{ color: '#888', fontSize: '0.9rem' }}>AI-Powered Insurance for Gig Workers</p>
        </div>
        
        {error && <div style={{ background: '#ef444433', border: '1px solid #ef4444', color: '#fca5a5', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.85rem' }}>{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '1rem' }}>
            <label style={{ color: '#aaa', fontSize: '0.85rem', display: 'block', marginBottom: '0.3rem' }}>Email</label>
            <div style={{ position: 'relative' }}>
              <Mail size={18} color="#666" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
              <input type="email" value={email} onChange={e => setEmail(e.target.value)} required
                style={{ width: '100%', padding: '0.75rem 0.75rem 0.75rem 2.5rem', background: 'rgba(255,255,255,0.08)', border: '1px solid rgba(255,255,255,0.15)', borderRadius: '8px', color: '#fff', fontSize: '0.95rem', boxSizing: 'border-box' }}
                placeholder="admin@earnsafe.com" />
            </div>
          </div>
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ color: '#aaa', fontSize: '0.85rem', display: 'block', marginBottom: '0.3rem' }}>Password</label>
            <div style={{ position: 'relative' }}>
              <Lock size={18} color="#666" style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)' }} />
              <input type="password" value={password} onChange={e => setPassword(e.target.value)} required
                style={{ width: '100%', padding: '0.75rem 0.75rem 0.75rem 2.5rem', background: 'rgba(255,255,255,0.08)', border: '1px solid rgba(255,255,255,0.15)', borderRadius: '8px', color: '#fff', fontSize: '0.95rem', boxSizing: 'border-box' }}
                placeholder="••••••" />
            </div>
          </div>
          <button type="submit" disabled={loading} style={{
            width: '100%', padding: '0.85rem', background: loading ? '#555' : 'linear-gradient(135deg, #4ade80, #22c55e)',
            border: 'none', borderRadius: '8px', color: '#000', fontWeight: 'bold', fontSize: '1rem', cursor: loading ? 'not-allowed' : 'pointer'
          }}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
        
        <p style={{ color: '#888', textAlign: 'center', marginTop: '1.5rem', fontSize: '0.85rem' }}>
          Don't have an account? <Link to="/register" style={{ color: '#4ade80', textDecoration: 'none' }}>Register</Link>
        </p>
      </div>
    </div>
  );
}
