import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import API from '../api/axios';
import { Shield, AlertTriangle, DollarSign, Activity } from 'lucide-react';
import { Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

export default function Dashboard() {
  const { user } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [purchasing, setPurchasing] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const res = await API.get('/dashboard/user');
      setDashboard(res.data);
    } catch (err) {
      setError('Failed to load dashboard');
    }
    setLoading(false);
  };

  const purchasePolicy = async () => {
    setPurchasing(true);
    try {
      await API.post('/policies/purchase', { coverageAmount: 5000 });
      fetchDashboard();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to purchase policy');
    }
    setPurchasing(false);
  };

  if (loading) return <div style={{ padding: '3rem', textAlign: 'center', color: '#fff', background: '#0f0c29', minHeight: '100vh' }}>Loading dashboard...</div>;

  const cardStyle = {
    background: 'rgba(255,255,255,0.05)', borderRadius: '12px', padding: '1.5rem',
    border: '1px solid rgba(255,255,255,0.1)'
  };

  const claimStatusData = dashboard ? [
    { name: 'Approved', value: dashboard.approvedClaims || 0 },
    { name: 'Pending', value: Math.max(0, (dashboard.totalClaims || 0) - (dashboard.approvedClaims || 0)) }
  ] : [];

  const COLORS = ['#4ade80', '#f59e0b'];

  return (
    <div style={{ minHeight: '100vh', background: 'linear-gradient(135deg, #0f0c29 0%, #1a1a2e 100%)', padding: '2rem' }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
        <h1 style={{ color: '#fff', marginBottom: '0.25rem' }}>Welcome, {dashboard?.fullName || user?.fullName}!</h1>
        <p style={{ color: '#888', marginBottom: '2rem' }}>Your insurance dashboard</p>
        
        {error && <div style={{ background: '#ef444433', color: '#fca5a5', padding: '1rem', borderRadius: '8px', marginBottom: '1rem' }}>{error}</div>}
        
        {/* Stats Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
          <div style={cardStyle}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
              <DollarSign size={20} color="#4ade80" />
              <span style={{ color: '#888', fontSize: '0.85rem' }}>Weekly Premium</span>
            </div>
            <div style={{ color: '#fff', fontSize: '1.8rem', fontWeight: 'bold' }}>₹{dashboard?.weeklyPremium?.toFixed(2) || '0.00'}</div>
          </div>
          
          <div style={cardStyle}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
              <Shield size={20} color="#60a5fa" />
              <span style={{ color: '#888', fontSize: '0.85rem' }}>Earnings Protected</span>
            </div>
            <div style={{ color: '#fff', fontSize: '1.8rem', fontWeight: 'bold' }}>₹{dashboard?.earningsProtected?.toFixed(2) || '0.00'}</div>
          </div>
          
          <div style={cardStyle}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
              <AlertTriangle size={20} color="#f59e0b" />
              <span style={{ color: '#888', fontSize: '0.85rem' }}>Total Claims</span>
            </div>
            <div style={{ color: '#fff', fontSize: '1.8rem', fontWeight: 'bold' }}>{dashboard?.totalClaims || 0}</div>
          </div>
          
          <div style={cardStyle}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
              <Activity size={20} color="#f472b6" />
              <span style={{ color: '#888', fontSize: '0.85rem' }}>Risk Score</span>
            </div>
            <div style={{ color: '#fff', fontSize: '1.8rem', fontWeight: 'bold' }}>{dashboard?.currentRiskScore?.toFixed(1) || '0.0'}</div>
          </div>
        </div>
        
        {/* Active Policy */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '2rem' }}>
          <div style={cardStyle}>
            <h3 style={{ color: '#fff', marginBottom: '1rem' }}>Active Policy</h3>
            {dashboard?.activePolicy ? (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                  <span style={{ color: '#888' }}>Coverage</span>
                  <span style={{ color: '#4ade80' }}>₹{dashboard.activePolicy.coverageAmount}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                  <span style={{ color: '#888' }}>Premium</span>
                  <span style={{ color: '#fff' }}>₹{dashboard.activePolicy.premiumAmount?.toFixed(2)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                  <span style={{ color: '#888' }}>Status</span>
                  <span style={{ color: '#4ade80', background: 'rgba(74,222,128,0.1)', padding: '0.15rem 0.5rem', borderRadius: '4px', fontSize: '0.8rem' }}>{dashboard.activePolicy.status}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                  <span style={{ color: '#888' }}>Expires</span>
                  <span style={{ color: '#fff', fontSize: '0.85rem' }}>{dashboard.activePolicy.endDate ? new Date(dashboard.activePolicy.endDate).toLocaleDateString() : 'N/A'}</span>
                </div>
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '1.5rem 0' }}>
                <p style={{ color: '#888', marginBottom: '1rem' }}>No active policy</p>
                <button onClick={purchasePolicy} disabled={purchasing} style={{
                  padding: '0.7rem 1.5rem', background: 'linear-gradient(135deg, #4ade80, #22c55e)',
                  border: 'none', borderRadius: '8px', color: '#000', fontWeight: 'bold', cursor: 'pointer'
                }}>
                  {purchasing ? 'Processing...' : 'Purchase Weekly Policy (₹5000 coverage)'}
                </button>
              </div>
            )}
          </div>
          
          <div style={cardStyle}>
            <h3 style={{ color: '#fff', marginBottom: '1rem' }}>Claim Status</h3>
            {claimStatusData.some(d => d.value > 0) ? (
              <ResponsiveContainer width="100%" height={180}>
                <PieChart>
                  <Pie data={claimStatusData} cx="50%" cy="50%" innerRadius={50} outerRadius={70} dataKey="value" label={({ name, value }) => `${name}: ${value}`}>
                    {claimStatusData.map((_, i) => <Cell key={i} fill={COLORS[i]} />)}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <p style={{ color: '#888', textAlign: 'center', padding: '2rem 0' }}>No claims yet</p>
            )}
          </div>
        </div>
        
        {/* Claims History */}
        <div style={cardStyle}>
          <h3 style={{ color: '#fff', marginBottom: '1rem' }}>Claims History</h3>
          {dashboard?.claimsHistory?.length > 0 ? (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
                    <th style={{ color: '#888', padding: '0.75rem', textAlign: 'left', fontSize: '0.8rem' }}>ID</th>
                    <th style={{ color: '#888', padding: '0.75rem', textAlign: 'left', fontSize: '0.8rem' }}>Type</th>
                    <th style={{ color: '#888', padding: '0.75rem', textAlign: 'left', fontSize: '0.8rem' }}>Amount</th>
                    <th style={{ color: '#888', padding: '0.75rem', textAlign: 'left', fontSize: '0.8rem' }}>Status</th>
                    <th style={{ color: '#888', padding: '0.75rem', textAlign: 'left', fontSize: '0.8rem' }}>Fraud Score</th>
                    <th style={{ color: '#888', padding: '0.75rem', textAlign: 'left', fontSize: '0.8rem' }}>Date</th>
                  </tr>
                </thead>
                <tbody>
                  {dashboard.claimsHistory.map(claim => (
                    <tr key={claim.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                      <td style={{ color: '#fff', padding: '0.75rem' }}>#{claim.id}</td>
                      <td style={{ color: '#fff', padding: '0.75rem' }}>{claim.disruptionType?.replace('_', ' ')}</td>
                      <td style={{ color: '#4ade80', padding: '0.75rem' }}>₹{claim.claimAmount?.toFixed(2)}</td>
                      <td style={{ padding: '0.75rem' }}>
                        <span style={{
                          color: claim.status === 'PAID' || claim.status === 'APPROVED' ? '#4ade80' : claim.status === 'REJECTED' ? '#ef4444' : '#f59e0b',
                          background: claim.status === 'PAID' || claim.status === 'APPROVED' ? 'rgba(74,222,128,0.1)' : claim.status === 'REJECTED' ? 'rgba(239,68,68,0.1)' : 'rgba(245,158,11,0.1)',
                          padding: '0.2rem 0.5rem', borderRadius: '4px', fontSize: '0.8rem'
                        }}>{claim.status}</span>
                      </td>
                      <td style={{ color: '#fff', padding: '0.75rem' }}>{claim.fraudScore?.toFixed(2) || 'N/A'}</td>
                      <td style={{ color: '#888', padding: '0.75rem', fontSize: '0.85rem' }}>{claim.triggeredAt ? new Date(claim.triggeredAt).toLocaleDateString() : 'N/A'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p style={{ color: '#888', textAlign: 'center', padding: '1.5rem 0' }}>No claims history</p>
          )}
        </div>
      </div>
    </div>
  );
}
