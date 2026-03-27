"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function DashboardPage() {
  const [user, setUser] = useState<any>(null);
  const [dashboardSummary, setDashboardSummary] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [showProfile, setShowProfile] = useState(false);
  const [staffInputs, setStaffInputs] = useState<Record<string, number>>({});
  const [examType, setExamType] = useState<string>("CIA1");

  const router = useRouter();

  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    if (!storedUser) router.push("/login");
    else {
      setUser(JSON.parse(storedUser));
      fetchSummary();
    }
  }, [router]);

  const fetchSummary = async () => {
    try {
      const token = localStorage.getItem('token');
      const res = await fetch("http://localhost:8080/api/dashboard/summary", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (res.ok) setDashboardSummary(await res.json());
    } catch (e) {
      console.error("Dashboard Out-of-Sync", e);
    } finally {
      setLoading(false);
    }
  };

  const handleStaffSave = async (subjectId: number) => {
    const payload = Object.keys(staffInputs).map(id => {
       const val = staffInputs[id];
       if (val === undefined || isNaN(val)) return null;
       const max = (examType === "MODEL") ? 100 : 60;
       if (val < 0 || val > max) throw new Error(`${examType} exceeds limit ${max}`);
       return { studentId: parseInt(id), subjectId, examType, marks: val };
    }).filter(p => p !== null);

    if (payload.length === 0) return alert("Select marks to sync.");

    try {
      const res = await fetch("http://localhost:8080/api/marks", {
        method: "POST",
        headers: { "Content-Type": "application/json", "Authorization": `Bearer ${localStorage.getItem('token')}` },
        body: JSON.stringify(payload)
      });
      if (res.ok) {
        alert("Persistence Complete.");
        setStaffInputs({});
        fetchSummary();
      } else alert(await res.text());
    } catch (e: any) { alert(e.message || "Network Error."); }
  };

  if (loading || !user || !dashboardSummary) return (
    <div style={{ height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#f8fafc' }}>
        <div style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '1.25rem', fontWeight: 600, color: 'var(--primary-color)', marginBottom: '1rem' }}>SUDHARSAN ACADEMIC CLOUD</div>
            <div style={{ fontSize: '0.9rem', color: '#64748b' }}>Accelerating performance analytics...</div>
        </div>
    </div>
  );

  const isStaff = user.role === "STAFF";
  const isCC = user.role === "CC";
  const isHOD = user.role === "HOD";

  return (
    <div className="dashboard-container" style={{ display: 'flex' }}>
      {/* Sidebar - Minimal and Fixed Subject Order Context */}
      <div className="sidebar">
        <div className="sidebar-title">Smart Grading</div>
        
        {isStaff && (
          <div style={{ padding: '0 1rem', marginBottom: '2rem' }}>
             <label style={{ fontSize: '0.75rem', fontWeight: 600, color: '#94a3b8' }}>Entry Context (Mode):</label>
             <select className="input-field" value={examType} onChange={(e)=>setExamType(e.target.value)} style={{ marginTop: '0.5rem', height: '35px' }}>
                <option>CIA1</option>
                <option>CIA2</option>
                <option>MODEL</option>
             </select>
          </div>
        )}

        {dashboardSummary.subjectRankings.map((sr: any, idx: number) => (
            <div key={idx} className="nav-item">
                <div style={{ fontSize: '0.6rem', color: '#94a3b8' }}>Subject #{idx + 1}</div>
                <div style={{ fontSize: '0.8rem', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{sr.subjectName}</div>
            </div>
        ))}

        <div style={{ marginTop: 'auto' }}>
            <div className="nav-item" onClick={() => { localStorage.clear(); router.push("/login"); }} style={{ color: 'var(--error)', fontWeight: 600 }}>Sign Out</div>
        </div>
      </div>

      <div className="main-content" style={{ flex: 1, padding: '2rem' }}>
           <div className="topbar">
              <h2 style={{ color: 'var(--primary-color)' }}>Final Summary Dashboard</h2>
              <div className="profile-card" onClick={() => setShowProfile(true)} style={{ cursor: 'pointer' }}>
                  <div style={{ textAlign: 'right' }}>
                      <div style={{ fontWeight: 600 }}>{user.name}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-light)' }}>{user.role} - Authorization Active</div>
                  </div>
                  <div className="avatar">{user.name.charAt(0)}</div>
              </div>
           </div>

           <div className="stats-grid">
               <div className="stat-card">
                  <div style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-light)' }}>SYSTEM AVERAGE</div>
                  <div className="stat-value">{dashboardSummary.statistics.globalAverage.toFixed(1)}</div>
               </div>
               <div className="stat-card">
                  <div style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-light)' }}>GLOBAL PASS RATE</div>
                  <div className="stat-value" style={{ color: 'var(--success)' }}>{dashboardSummary.statistics.globalPassRate.toFixed(1)}%</div>
               </div>
               {(isHOD || isCC) && (
                   <div className="stat-card" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <button className="btn-primary" onClick={() => {
                        fetch("http://localhost:8080/api/pdf/download", { headers: { "Authorization" : `Bearer ${localStorage.getItem('token')}` }})
                        .then(res => res.blob()).then(blob => window.URL.createObjectURL(blob))
                        .then(url => { const a = document.createElement("a"); a.href=url; a.download="Final_Grade_Report.pdf"; a.click(); })
                      }} style={{ background: 'var(--success)' }}>GENERATE PDF REPORT</button>
                   </div>
               )}
           </div>

           {/* SUBJECT-WISE RANKING SYSTEM (MANDATORY) */}
           <div style={{ display: 'flex', flexDirection: 'column', gap: '2.5rem' }}>
                {dashboardSummary.subjectRankings.map((sr: any, idx: number) => {
                    // Actually, I'll allow everyone to see the summary but Staff sees the input fields for theirs
                    return (
                        <div key={idx} style={{ background: 'white', borderLeft: '5px solid var(--primary-color)', borderRadius: '1rem', padding: '1.5rem', boxShadow: '0 4px 6px rgba(0,0,0,0.05)' }}>
                             <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', borderBottom: '1px solid #f1f5f9', paddingBottom: '1rem' }}>
                                 <div>
                                     <div style={{ fontSize: '0.75rem', color: '#94a3b8', fontWeight: 700 }}>SUBJECT RANKING #{idx+1}</div>
                                     <h3 style={{ margin: 0, color: 'var(--text-dark)' }}>{sr.subjectName}</h3>
                                 </div>
                                 <div style={{ textAlign: 'right' }}>
                                     <div style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--primary-color)' }}>{sr.passPercentage.toFixed(1)}% Pass</div>
                                     <div style={{ fontSize: '0.75rem', color: 'var(--text-light)' }}>Subject Avg: {sr.average.toFixed(1)}</div>
                                 </div>
                             </div>

                             <table style={{ boxShadow: 'none' }}>
                                <thead>
                                    <tr>
                                        <th style={{ width: '80px' }}>Rank</th>
                                        <th>Registration No.</th>
                                        <th>Student Name</th>
                                        <th>Subject Mark (Total)</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {sr.ranking.map((row: any, rIdx: number) => {
                                        const isStaffForThisSubject = isStaff && (
                                            (user.name.toLowerCase().includes("aarthi") && sr.subjectName.toLowerCase().includes("cloud")) ||
                                            (user.name.toLowerCase().includes("ayyapan") && sr.subjectName.toLowerCase().includes("multimedia")) ||
                                            (user.name.toLowerCase().includes("siva") && sr.subjectName.toLowerCase().includes("network")) ||
                                            (user.name.toLowerCase().includes("suga") && sr.subjectName.toLowerCase().includes("storage")) ||
                                            (user.name.toLowerCase().includes("elambarathi") && sr.subjectName.toLowerCase().includes("software")) ||
                                            (user.name.toLowerCase().includes("indu") && sr.subjectName.toLowerCase().includes("embedded"))
                                        );

                                        return (
                                            <tr key={rIdx} className={row.username === user.username ? "highlight-student" : ""}>
                                                <td style={{ fontWeight: 700, fontSize: '1.1rem' }}>
                                                    {rIdx === 0 ? "🥇" : rIdx === 1 ? "🥈" : rIdx === 2 ? "🥉" : rIdx + 1}
                                                </td>
                                                <td>{row.username}</td>
                                                <td>{row.name}</td>
                                                <td>
                                                    {isStaffForThisSubject ? (
                                                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                                            <input 
                                                                type="number" 
                                                                className="input-field" 
                                                                style={{ width: '80px', height: '35px', padding: '0 8px' }}
                                                                placeholder={examType}
                                                                value={staffInputs[row.studentId] ?? ""}
                                                                onChange={(e) => setStaffInputs({...staffInputs, [row.studentId]: parseInt(e.target.value)})}
                                                            />
                                                            <button onClick={() => handleStaffSave(sr.subjectId)} className="btn-primary" style={{ width: 'auto', padding: '4px 12px', fontSize: '0.75rem' }}>Update</button>
                                                        </div>
                                                    ) : (
                                                        <>
                                                            <div style={{ fontWeight: 700 }}>{row.marks} pts</div>
                                                            <div style={{ fontSize: '0.7rem', color: '#94a3b8' }}>Max possible: 220</div>
                                                        </>
                                                    )}
                                                </td>
                                                <td className={row.pass ? "status-pass" : "status-fail"}>{row.pass ? "PASS" : "FAIL"}</td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                             </table>
                        </div>
                    );
                })}
           </div>
      </div>

      {/* Right Drawer Profile - No Year Field */}
      {showProfile && (
          <div style={{ position: 'fixed', top: 0, right: 0, height: '100vh', width: '320px', background: 'white', zIndex: 1000, boxShadow: '-10px 0 30px rgba(0,0,0,0.1)', padding: '2rem', display: 'flex', flexDirection: 'column' }}>
             <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2.5rem' }}>
                <h3 style={{ margin: 0 }}>Smart Profile</h3>
                <button onClick={() => setShowProfile(false)} style={{ border: 'none', background: 'none', fontSize: '1.5rem', cursor: 'pointer' }}>×</button>
             </div>
             <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
                <div className="avatar" style={{ width: '90px', height: '90px', fontSize: '2.5rem', margin: '0 auto 1.5rem' }}>{user.name.charAt(0)}</div>
                <h4 style={{ margin: '0.5rem 0' }}>{user.name}</h4>
                <div style={{ color: 'var(--text-light)', fontSize: '0.85rem' }}>{user.role} Certification Active</div>
             </div>
             <div style={{ borderTop: '1px solid #f1f5f9', paddingTop: '1.5rem' }}>
                <p style={{ margin: '15px 0' }}>📂 <b>Department:</b> {user.department}</p>
                <p style={{ margin: '15px 0' }}>🆔 <b>Ident:</b> {user.username}</p>
             </div>
             <button onClick={() => { localStorage.clear(); router.push("/login"); }} className="btn-primary" style={{ marginTop: 'auto', background: 'var(--error)' }}>Terminate Session</button>
          </div>
      )}
      {showProfile && <div onClick={() => setShowProfile(false)} style={{ position: 'fixed', top: 0, left: 0, width: '100vw', height: '100vh', background: 'rgba(0,0,0,0.3)', zIndex: 999 }}></div>}
    </div>
  );
}
