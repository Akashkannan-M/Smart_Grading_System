"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function DashboardPage() {
  const [user, setUser] = useState<any>(null);
  const [examType, setExamType] = useState("CIA1");
  const [year, setYear] = useState("1st Year");
  const router = useRouter();

  useEffect(() => {
    const usr = localStorage.getItem("user");
    if (!usr) {
      router.push("/login");
    } else {
      setUser(JSON.parse(usr));
    }
  }, [router]);

  if (!user) return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>;

  const handleLogout = () => {
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    router.push("/login");
  };

  const downloadPdf = async () => {
    try {
      const res = await fetch(`http://localhost:8080/api/pdf/download?type=${examType}`, {
        headers: { "Authorization": `Bearer ${localStorage.getItem('token')}` }
      });
      const blob = await res.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${examType}_results.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
    } catch(err) {
      console.error(err);
    }
  };

  return (
    <div className="dashboard-container">
      <div className="sidebar">
        <div className="sidebar-title">Smart Grading</div>
        <div 
          className={`nav-item ${examType === 'CIA1' ? 'active' : ''}`}
          onClick={() => setExamType('CIA1')}
        >CIA1</div>
        <div 
          className={`nav-item ${examType === 'CIA2' ? 'active' : ''}`}
          onClick={() => setExamType('CIA2')}
        >CIA2</div>
        <div 
          className={`nav-item ${examType === 'MODEL' ? 'active' : ''}`}
          onClick={() => setExamType('MODEL')}
        >Model Exam</div>
        
        <div style={{ marginTop: 'auto' }}>
          <div className="nav-item" onClick={handleLogout} style={{ color: 'var(--error)' }}>
            Logout
          </div>
        </div>
      </div>

      <div className="main-content">
        <div className="topbar">
          <div>
            <h2 style={{ margin: 0 }}>Dashboard - {examType}</h2>
            {(user.role === 'HOD') && (
              <select 
                value={year} 
                onChange={(e) => setYear(e.target.value)}
                style={{ marginTop: '0.5rem', padding: '0.25rem' }}
              >
                <option>1st Year</option>
                <option>2nd Year</option>
                <option>3rd Year</option>
                <option>4th Year</option>
              </select>
            )}
          </div>
          <div className="profile-card">
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontWeight: 600 }}>{user.name} ({user.username})</div>
              <div style={{ fontSize: '0.875rem', color: 'var(--text-light)' }}>
                {user.role} - {user.department}
              </div>
            </div>
            <div className="avatar">
              {user.name.charAt(0)}
            </div>
          </div>
        </div>

        <div className="stats-grid">
          <div className="stat-card">
            <div style={{ color: 'var(--text-light)', marginBottom: '0.5rem' }}>Class Average</div>
            <div className="stat-value">78%</div>
          </div>
          <div className="stat-card">
            <div style={{ color: 'var(--text-light)', marginBottom: '0.5rem' }}>Pass Percentage</div>
            <div className="stat-value" style={{ color: 'var(--success)' }}>92%</div>
          </div>
          <div className="stat-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
            <button className="btn-primary" onClick={downloadPdf}>
              Export PDF
            </button>
          </div>
        </div>

        <div style={{ backgroundColor: 'var(--card-bg)', padding: '1.5rem', borderRadius: '0.75rem' }}>
          <h3 style={{ marginTop: 0, marginBottom: '1rem' }}>Student Rankings</h3>
          <table>
            <thead>
              <tr>
                <th>Rank</th>
                <th>Reg No</th>
                <th>Name</th>
                <th>Marks</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {/* Dummy data to demonstrate UI elements requested */}
              <tr>
                <td style={{ fontSize: '1.25rem' }}>🥇 Gold</td>
                <td>2024001</td>
                <td>Student 001</td>
                <td>98/100</td>
                <td className="status-pass">PASS</td>
              </tr>
              <tr>
                <td style={{ fontSize: '1.25rem' }}>🥈 Silver</td>
                <td>2024002</td>
                <td>Student 002</td>
                <td>95/100</td>
                <td className="status-pass">PASS</td>
              </tr>
              <tr>
                <td style={{ fontSize: '1.25rem' }}>🥉 Bronze</td>
                <td>2024003</td>
                <td>Student 003</td>
                <td>90/100</td>
                <td className="status-pass">PASS</td>
              </tr>
              <tr className="highlight-fail">
                <td>24</td>
                <td>2024024</td>
                <td>Student 024</td>
                <td>32/100</td>
                <td className="status-fail">FAIL</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
