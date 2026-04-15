import { useState, useEffect } from 'react';
import API from '../api/axios';
import { Users, FileText, AlertTriangle, TrendingUp, DollarSign, ShieldAlert } from 'lucide-react';
import { Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const res = await API.get('/admin/dashboard');
      setStats(res.data);
    } catch (err) {
      console.error('Failed to load admin dashboard');
    }
    setLoading(false);
  };

  if (loading) return <div style={{ padding: '3rem', textAlign: 'center', color: '#fff', background: '#0f0c29', minHeight: '100vh' }}>Loading admin dashboard...</div>;

  const cardStyle = {
    background: 'rgba(255,255,255,0.05)', borderRadius: '12px', padding: '1.25rem',
    border: '1px solid rgba(255,255,255,0.1)'
  };

  const claimsPieData = stats ? [
    { name: 'Approved', value: stats.approvedClaims || 0 },
    { name: 'Rejected', value: stats.rejectedClaims || 0 },
    { name: 'Pending', value: Math.max(0, (stats.totalClaims || 0) - (stats.approvedClaims || 0) - (stats.rejectedClaims || 0)) }
  ].filter(d => d.value > 0) : [];

  const COLORS = ['#4ade80', '#ef4444', '#f59e0b', '#60a5fa'];

  const fraudData = stats ? [
    { name: 'Clean', value: Math.max(0, (stats.totalClaims || 0) - (stats.fraudulentClaims || 0)) },
    { name: 'Fraudulent', value: stats.fraudulentClaims || 0 }
  ].filter(d => d.value > 0) : [];

  return (
    <div style={{ minHeight: '100vh', background: 'linear-gradient(135deg, #0f0c29 0%, #1a1a2e 100%)', padding: '2rem' }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <h1 style={{ color: '#fff', marginBottom: '0.25rem' }}>Admin Dashboard</h1>
        <p style={{ color: '#888', marginBottom: '2rem' }}>Platform analytics & monitoring</p>
        
        {/* Stats Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
          {[
            { icon: <Users size={20} color="#60a5fa" />, label: 'Total Users', value: stats?.totalUsers || 0, color: '#60a5fa' },
            { icon: <FileText size={20} color="#4ade80" />, label: 'Active Policies', value: stats?.activePolicies || 0, color: '#4ade80' },
            { icon: <AlertTriangle size={20} color="#f59e0b" />, label: 'Total Claims', value: stats?.totalClaims || 0, color: '#f59e0b' },
            { icon: <ShieldAlert size={20} color="#ef4444" />, label: 'Fraud Detected', value: stats?.fraudulentClaims || 0, color: '#ef4444' },
            { icon: <DollarSign size={20} color="#4ade80" />, label: 'Premium Collected', value: `₹${stats?.totalPremiumCollected?.toFixed(0) || 0}`, color: '#4ade80' },
            { icon: <TrendingUp size={20} color="#f472b6" />, label: 'Total Payouts', value: `₹${stats?.totalPayouts?.toFixed(0) || 0}`, color: '#f472b6' }
          ].map((item, i) => (
            <div key={i} style={cardStyle}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.4rem' }}>
                {item.icon}
                <span style={{ color: '#888', fontSize: '0.75rem' }}>{item.label}</span>
              </div>
              <div style={{ color: '#fff', fontSize: '1.5rem', fontWeight: 'bold' }}>{item.value}</div>
            </div>
          ))}
        </div>
        
        {/* Charts */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '2rem' }}>
          <div style={cardStyle}>
            <h3 style={{ color: '#fff', marginBottom: '1rem' }}>Claims Analytics</h3>
            {claimsPieData.length > 0 ? (
              <ResponsiveContainer width="100%" height={220}>
                <PieChart>
                  <Pie data={claimsPieData} cx="50%" cy="50%" outerRadius={80} dataKey="value" label={({ name, value }) => `${name}: ${value}`}>
                    {claimsPieData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            ) : <p style={{ color: '#888', textAlign: 'center', padding: '3rem 0' }}>No data</p>}
          </div>
          
          <div style={cardStyle}>
            <h3 style={{ color: '#fff', marginBottom: '1rem' }}>Fraud Detection</h3>
            {fraudData.length > 0 ? (
              <ResponsiveContainer width="100%" height={220}>
                <PieChart>
                  <Pie data={fraudData} cx="50%" cy="50%" outerRadius={80} dataKey="value" label={({ name, value }) => `${name}: ${value}`}>
                    {fraudData.map((_, i) => <Cell key={i} fill={i === 0 ? '#4ade80' : '#ef4444'} />)}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            ) : <p style={{ color: '#888', textAlign: 'center', padding: '3rem 0' }}>No data</p>}
          </div>
        </div>
        
        {/* Recent Claims */}
        <div style={cardStyle}>
          <h3 style={{ color: '#fff', marginBottom: '1rem' }}>Recent Claims</h3>
          {stats?.recentClaims?.length > 0 ? (
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
                  <th style={{ color: '#888', padding: '0.6rem', textAlign: 'left', fontSize: '0.8rem' }}>ID</th>
                  <th style={{ color: '#888', padding: '0.6rem', textAlign: 'left', fontSize: '0.8rem' }}>User</th>
                  <th style={{ color: '#888', padding: '0.6rem', textAlign: 'left', fontSize: '0.8rem' }}>Type</th>
                  <th style={{ color: '#888', padding: '0.6rem', textAlign: 'left', fontSize: '0.8rem' }}>Amount</th>
                  <th style={{ color: '#888', padding: '0.6rem', textAlign: 'left', fontSize: '0.8rem' }}>Status</th>
                  <th style={{ color: '#888', padding: '0.6rem', textAlign: 'left', fontSize: '0.8rem' }}>Fraud</th>
                </tr>
              </thead>
              <tbody>
                {stats.recentClaims.map(c => (
                  <tr key={c.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                    <td style={{ color: '#fff', padding: '0.6rem' }}>#{c.id}</td>
                    <td style={{ color: '#fff', padding: '0.6rem' }}>{c.userName}</td>
                    <td style={{ color: '#fff', padding: '0.6rem' }}>{c.disruptionType?.replace('_', ' ')}</td>
                    <td style={{ color: '#4ade80', padding: '0.6rem' }}>₹{c.claimAmount?.toFixed(2)}</td>
                    <td style={{ padding: '0.6rem' }}>
                      <span style={{
                        color: c.status === 'PAID' || c.status === 'APPROVED' ? '#4ade80' : c.status === 'REJECTED' ? '#ef4444' : '#f59e0b',
                        fontSize: '0.8rem'
                      }}>{c.status}</span>
                    </td>
                    <td style={{ padding: '0.6rem' }}>
                      <span style={{ color: c.isFraudulent ? '#ef4444' : '#4ade80', fontSize: '0.8rem' }}>
                        {c.isFraudulent ? '⚠ Fraud' : '✓ Clean'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p style={{ color: '#888', textAlign: 'center', padding: '1.5rem 0' }}>No recent claims</p>
          )}
        </div>
      </div>
    </div>
  );
}
