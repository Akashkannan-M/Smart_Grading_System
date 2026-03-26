"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function ProfilePage() {
  const [user, setUser] = useState<any>(null);
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

  return (
    <div className="dashboard-container">
      <div className="sidebar" style={{ minWidth: '220px' }}>
        <div className="sidebar-title" style={{ fontSize: '1.3rem' }}>Smart Grading</div>
        <div className="nav-item" onClick={() => router.push("/dashboard")}>
          Back to Dashboard
        </div>
        <div style={{ marginTop: 'auto' }}>
          <div className="nav-item" onClick={() => { localStorage.clear(); router.push("/login"); }} style={{ color: 'var(--error)' }}>
            Logout
          </div>
        </div>
      </div>

      <div className="main-content">
        <div className="topbar">
          <h2 style={{ margin: 0, color: 'var(--primary-color)' }}>Personal Profile</h2>
          <div className="profile-card">
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontWeight: 600 }}>{user.name}</div>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-light)' }}>{user.role}</div>
              </div>
              <div className="avatar">{user.name.charAt(0)}</div>
          </div>
        </div>

        <div style={{ maxWidth: '600px', margin: '2rem auto', background: 'white', padding: '2rem', borderRadius: '1rem', boxShadow: '0 4px 20px rgba(0,0,0,0.05)' }}>
          <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
              <div style={{ width: '80px', height: '80px', borderRadius: '50%', backgroundColor: 'var(--primary-color)', color: 'white', fontSize: '2rem', fontWeight: 600, display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1rem' }}>
                {user.name.charAt(0)}
              </div>
              <h2 style={{ margin: 0 }}>{user.name}</h2>
              <p style={{ color: 'var(--text-light)' }}>{user.role} - {user.department}</p>
          </div>

          <div style={{ borderTop: '1px solid #f1f5f9', paddingTop: '1.5rem' }}>
             <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', color: 'var(--text-light)', marginBottom: '0.3rem' }}>USERNAME / REGISTRATION NO.</label>
                <div style={{ fontSize: '1.1rem', fontWeight: 500 }}>{user.username}</div>
             </div>
             
             <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', color: 'var(--text-light)', marginBottom: '0.3rem' }}>DEPARTMENT</label>
                <div style={{ fontSize: '1.1rem', fontWeight: 500 }}>{user.department}</div>
             </div>

             <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', fontSize: '0.85rem', color: 'var(--text-light)', marginBottom: '0.3rem' }}>EMPLOYEE ID / SYSTEM ID</label>
                <div style={{ fontSize: '1.1rem', fontWeight: 500 }}>#{user.id.toString().padStart(4, '0')}</div>
             </div>
          </div>
        </div>
      </div>
    </div>
  );
}
