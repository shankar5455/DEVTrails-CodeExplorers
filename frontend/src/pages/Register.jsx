import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield } from 'lucide-react';

export default function Register() {
  const [form, setForm] = useState({
    fullName: '', email: '', password: '', phone: '',
    latitude: 19.0760, longitude: 72.8777, city: 'Mumbai'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: name === 'latitude' || name === 'longitude' ? parseFloat(value) || 0 : value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await register(form);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    }
    setLoading(false);
  };

  const inputStyle = {
    width: '100%', padding: '0.7rem', background: 'rgba(255,255,255,0.08)',
    border: '1px solid rgba(255,255,255,0.15)', borderRadius: '8px',
    color: '#fff', fontSize: '0.9rem', boxSizing: 'border-box'
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%)', padding: '2rem'
    }}>
      <div style={{
        background: 'rgba(255,255,255,0.05)', backdropFilter: 'blur(10px)',
        borderRadius: '16px', padding: '2.5rem', width: '100%', maxWidth: '480px',
        border: '1px solid rgba(255,255,255,0.1)'
      }}>
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <Shield size={42} color="#4ade80" />
          <h2 style={{ color: '#fff', margin: '0.5rem 0' }}>Create Account</h2>
        </div>
        
        {error && <div style={{ background: '#ef444433', border: '1px solid #ef4444', color: '#fca5a5', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.85rem' }}>{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '0.75rem' }}>
            <label style={{ color: '#aaa', fontSize: '0.8rem' }}>Full Name</label>
            <input name="fullName" value={form.fullName} onChange={handleChange} required style={inputStyle} placeholder="Rahul Kumar" />
          </div>
          <div style={{ marginBottom: '0.75rem' }}>
            <label style={{ color: '#aaa', fontSize: '0.8rem' }}>Email</label>
            <input name="email" type="email" value={form.email} onChange={handleChange} required style={inputStyle} placeholder="rahul@example.com" />
          </div>
          <div style={{ marginBottom: '0.75rem' }}>
            <label style={{ color: '#aaa', fontSize: '0.8rem' }}>Password</label>
            <input name="password" type="password" value={form.password} onChange={handleChange} required style={inputStyle} placeholder="Min 6 characters" />
          </div>
          <div style={{ marginBottom: '0.75rem' }}>
            <label style={{ color: '#aaa', fontSize: '0.8rem' }}>Phone</label>
            <input name="phone" value={form.phone} onChange={handleChange} style={inputStyle} placeholder="+91-9876543210" />
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', marginBottom: '0.75rem' }}>
            <div>
              <label style={{ color: '#aaa', fontSize: '0.8rem' }}>Latitude</label>
              <input name="latitude" type="number" step="any" value={form.latitude} onChange={handleChange} required style={inputStyle} />
            </div>
            <div>
              <label style={{ color: '#aaa', fontSize: '0.8rem' }}>Longitude</label>
              <input name="longitude" type="number" step="any" value={form.longitude} onChange={handleChange} required style={inputStyle} />
            </div>
          </div>
          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ color: '#aaa', fontSize: '0.8rem' }}>City</label>
            <input name="city" value={form.city} onChange={handleChange} style={inputStyle} placeholder="Mumbai" />
          </div>
          <button type="submit" disabled={loading} style={{
            width: '100%', padding: '0.8rem', background: loading ? '#555' : 'linear-gradient(135deg, #4ade80, #22c55e)',
            border: 'none', borderRadius: '8px', color: '#000', fontWeight: 'bold', fontSize: '1rem', cursor: loading ? 'not-allowed' : 'pointer'
          }}>
            {loading ? 'Creating account...' : 'Create Account'}
          </button>
        </form>
        
        <p style={{ color: '#888', textAlign: 'center', marginTop: '1rem', fontSize: '0.85rem' }}>
          Already have an account? <Link to="/login" style={{ color: '#4ade80', textDecoration: 'none' }}>Sign In</Link>
        </p>
      </div>
    </div>
  );
}
