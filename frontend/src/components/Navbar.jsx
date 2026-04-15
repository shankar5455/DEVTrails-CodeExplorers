import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield, LogOut, LayoutDashboard, User } from 'lucide-react';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) return null;

  return (
    <nav style={{
      background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 100%)',
      padding: '0 2rem',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      height: '64px',
      boxShadow: '0 2px 10px rgba(0,0,0,0.3)'
    }}>
      <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', textDecoration: 'none' }}>
        <Shield size={28} color="#4ade80" />
        <span style={{ color: '#fff', fontSize: '1.4rem', fontWeight: 'bold' }}>EarnSafe</span>
      </Link>
      
      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        <Link to="/dashboard" style={{ color: '#ccc', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
          <LayoutDashboard size={18} /> Dashboard
        </Link>
        {user.role === 'ADMIN' && (
          <Link to="/admin" style={{ color: '#ccc', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
            <User size={18} /> Admin
          </Link>
        )}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <span style={{ color: '#4ade80', fontSize: '0.9rem' }}>{user.fullName}</span>
          <button onClick={handleLogout} style={{
            background: 'transparent', border: '1px solid #555', color: '#ccc',
            padding: '0.4rem 0.8rem', borderRadius: '6px', cursor: 'pointer',
            display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.85rem'
          }}>
            <LogOut size={16} /> Logout
          </button>
        </div>
      </div>
    </nav>
  );
}
