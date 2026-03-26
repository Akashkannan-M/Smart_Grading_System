"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function DashboardPage() {
  const [user, setUser] = useState<any>(null);
  const [examType, setExamType] = useState<string>("CIA1");
  const [students, setStudents] = useState<any[]>([]);
  const [subjects, setSubjects] = useState<any[]>([]);
  const [marks, setMarks] = useState<any[]>([]);
  const [staffInputs, setStaffInputs] = useState<Record<string, number>>({});
  const [isClient, setIsClient] = useState(false);
  const [activeTab, setActiveTab] = useState<"MY_MARKS" | "CLASS_RANKING">("MY_MARKS");
  const [showProfile, setShowProfile] = useState(false);

  const router = useRouter();

  useEffect(() => {
    setIsClient(true);
    const storedUser = localStorage.getItem("user");
    if (!storedUser) router.push("/login");
    else setUser(JSON.parse(storedUser));
  }, [router]);

  useEffect(() => {
    if (user) fetchData();
  }, [user]);

  const fetchData = async () => {
    try {
      const headers = { "Authorization": `Bearer ${localStorage.getItem('token')}` };
      const [stdRes, subRes, mrkRes] = await Promise.all([
        fetch("http://localhost:8080/api/marks/students", { headers }),
        fetch("http://localhost:8080/api/marks/subjects", { headers }),
        fetch("http://localhost:8080/api/marks/all", { headers })
      ]);

      if (stdRes.ok) setStudents(await stdRes.json());
      if (subRes.ok) setSubjects(await subRes.json());
      if (mrkRes.ok) setMarks(await mrkRes.json());
    } catch (e) {
      console.error("Fetch Error:", e);
    }
  };

  const handleSaveMarks = async () => {
    const assignedSub = subjects.find(s => s.staff && s.staff.id === user.id);
    if (!assignedSub) return alert("Error: No assigned subject.");

    const payload = Object.keys(staffInputs).map(id => ({
      studentId: parseInt(id),
      subjectId: assignedSub.id,
      examType: examType,
      marks: staffInputs[id]
    }));

    try {
      const res = await fetch("http://localhost:8080/api/marks", {
        method: "POST",
        headers: { 
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem('token')}` 
        },
        body: JSON.stringify(payload)
      });
      if (res.ok) {
        alert("Mark entry successful!");
        fetchData();
      } else alert(await res.text());
    } catch (e) {
      alert("Persistence Failure.");
    }
  };

  if (!isClient || !user) return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading Hub...</div>;

  // AGGREGATION LOGIC (Real-time Calculation)
  const rankingList = students.map(std => {
    const stdMarks = marks.filter(m => m.studentId === std.id);
    const cia1 = stdMarks.find(m => m.examType === "CIA1")?.marks || 0;
    const cia2 = stdMarks.find(m => m.examType === "CIA2")?.marks || 0;
    const model = stdMarks.find(m => m.examType === "MODEL")?.marks || 0;
    const finalTotal = cia1 + cia2 + model;
    const isMainPass = cia1 >= 30 && cia2 >= 30 && model >= 45;
    return { ...std, cia1, cia2, model, finalTotal, isMainPass };
  });

  // Global ranking sorted by Final Total
  rankingList.sort((a, b) => b.finalTotal - a.finalTotal);

  // Statistics for Current Exam View
  const currentExamMarks = marks.filter(m => m.examType === examType);
  const avgMarks = currentExamMarks.length > 0 
    ? (currentExamMarks.reduce((a, b) => a + b.marks, 0) / currentExamMarks.length).toFixed(1) 
    : "0.0";
  const passPercent = currentExamMarks.length > 0 
    ? ((currentExamMarks.filter(m => m.isPass).length / currentExamMarks.length) * 100).toFixed(1) 
    : "0.0";

  const me = rankingList.find(s => s.id === user.id);
  const myRank = rankingList.findIndex(s => s.id === user.id) + 1;

  const downloadPdf = async () => {
    const res = await fetch("http://localhost:8080/api/pdf/download", {
        headers: { "Authorization": `Bearer ${localStorage.getItem('token')}` }
    });
    const blob = await res.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "Final_Report_Card.pdf";
    document.body.appendChild(link);
    link.click();
  };

  return (
    <div className="dashboard-container" style={{ position: 'relative' }}>
      <div className="sidebar">
        <div className="sidebar-title">Sudharsan Grading</div>
        
        <div className={`nav-item ${examType === "CIA1" ? "active" : ""}`} onClick={() => setExamType("CIA1")}>CIA 1 (Max 60)</div>
        <div className={`nav-item ${examType === "CIA2" ? "active" : ""}`} onClick={() => setExamType("CIA2")}>CIA 2 (Max 60)</div>
        <div className={`nav-item ${examType === "MODEL" ? "active" : ""}`} onClick={() => setExamType("MODEL")}>Model (Max 100)</div>
        
        <div style={{ marginTop: 'auto' }}>
            <div className="nav-item" onClick={() => { localStorage.clear(); router.push("/login"); }} style={{ color: 'var(--error)' }}>Sign Out</div>
        </div>
      </div>

      <div className="main-content">
        <div className="topbar">
          <h2 style={{ color: 'var(--primary-color)' }}>{examType} Analysis View</h2>
          
          <div style={{ position: 'relative' }}>
            <div className="profile-card" onClick={() => setShowProfile(!showProfile)}>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontWeight: 600 }}>{user.name}</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-light)' }}>{user.role}</div>
              </div>
              <div className="avatar">{user.name.charAt(0)}</div>
            </div>

            {showProfile && (
              <div style={{ position: 'absolute', top: '100%', right: '0', background: 'white', border: '1px solid #ddd', borderRadius: '8px', padding: '1rem', zIndex: 100, minWidth: '200px', marginTop: '0.5rem', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
                 <div style={{ fontWeight: 600, borderBottom: '1px solid #eee', paddingBottom: '0.5rem', marginBottom: '0.5rem' }}>User Profile</div>
                 <div style={{ fontSize: '0.85rem' }}>
                    <p style={{ margin: '4px 0' }}>💡 Name: {user.name}</p>
                    <p style={{ margin: '4px 0' }}>💼 Role: {user.role}</p>
                    <p style={{ margin: '4px 0' }}>🏛 Dept: {user.department}</p>
                 </div>
                 <button onClick={() => setShowProfile(false)} style={{ marginTop: '1rem', width: '100%', padding: '0.4rem', borderRadius: '4px', background: '#f5f5f5', border: '1px solid #ddd', cursor: 'pointer' }}>Close Modal</button>
              </div>
            )}
          </div>
        </div>

        <div className="stats-grid">
          <div className="stat-card">
            <div style={{ fontSize: '0.8rem', color: 'var(--text-light)', fontWeight: 600 }}>{user.role === "STUDENT" ? "MY GLOBAL RANK" : "CLASS AVERAGE"}</div>
            <div className="stat-value">{user.role === "STUDENT" ? `#${myRank}` : `${avgMarks}`}</div>
          </div>
          <div className="stat-card">
            <div style={{ fontSize: '0.8rem', color: 'var(--text-light)', fontWeight: 600 }}>{user.role === "STUDENT" ? "AGGREGATE TOTAL" : "PASS PERCENTAGE"}</div>
            <div className="stat-value" style={{ color: 'var(--success)' }}>{user.role === "STUDENT" ? `${me?.finalTotal}/220` : `${passPercent}%`}</div>
          </div>
          <div className="stat-card" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <button className="btn-primary" onClick={downloadPdf}>Download Final Report</button>
          </div>
        </div>

        {user.role === "STUDENT" && (
            <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem' }}>
                <button 
                  onClick={() => setActiveTab("MY_MARKS")}
                  style={{ padding: '0.5rem 1.5rem', borderRadius: '4px', border: 'none', background: activeTab === "MY_MARKS" ? 'var(--primary-color)' : '#ddd', color: activeTab === "MY_MARKS" ? 'white' : 'black', cursor: 'pointer', fontWeight: 600 }}
                >My Subject Marks</button>
                <button 
                  onClick={() => setActiveTab("CLASS_RANKING")}
                  style={{ padding: '0.5rem 1.5rem', borderRadius: '4px', border: 'none', background: activeTab === "CLASS_RANKING" ? 'var(--primary-color)' : '#ddd', color: activeTab === "CLASS_RANKING" ? 'white' : 'black', cursor: 'pointer', fontWeight: 600 }}
                >Global Class Ranking</button>
            </div>
        )}

        <div style={{ background: 'white', padding: '1.5rem', borderRadius: '12px', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem' }}>
            <h3 style={{ margin: 0 }}>
                {user.role === "STAFF" ? `Update Performance: ${examType}` : 
                 activeTab === "MY_MARKS" ? "My Personal Academic Score" : "Global Leaderboard"}
            </h3>
            {user.role === "STAFF" && <button className="btn-primary" onClick={handleSaveMarks} style={{ padding: '0.4rem 1rem', background: 'var(--warning)' }}>Confirm Marks Persistence</button>}
          </div>

          <table>
            <thead>
              <tr>
                <th>{activeTab === "MY_MARKS" && user.role === "STUDENT" ? "Subject" : "Rank"}</th>
                <th>Register No.</th>
                <th>Name</th>
                <th>{user.role === "STAFF" ? "Input Marks" : "Score"}</th>
                <th>{user.role === "STAFF" ? "Threshold" : "Status"}</th>
              </tr>
            </thead>
            <tbody>
              {/* Table logic based on role and tab */}
              {(user.role === "STUDENT" && activeTab === "MY_MARKS") ? (
                  rankingList.filter(s => s.id === user.id).map(s => (
                    <tr key={s.id} className="highlight-student">
                       <td>CORE CSE</td>
                       <td>{s.username}</td>
                       <td>{s.name}</td>
                       <td>{examType === "CIA1" ? s.cia1 : examType === "CIA2" ? s.cia2 : s.model}</td>
                       <td className={
                           (examType === "CIA1" && s.cia1 >= 30) || 
                           (examType === "CIA2" && s.cia2 >= 30) ||
                           (examType === "MODEL" && s.model >= 45) ? "status-pass" : "status-fail"
                       }>
                           {(examType === "CIA1" && s.cia1 >= 30) || 
                            (examType === "CIA2" && s.cia2 >= 30) ||
                            (examType === "MODEL" && s.model >= 45) ? "PASS" : "FAIL"}
                       </td>
                    </tr>
                  ))
              ) : (
                  rankingList.map((s, idx) => (
                    <tr key={s.id} className={user.id === s.id ? "highlight-student" : ""}>
                        <td>{idx + 1}</td>
                        <td>{s.username}</td>
                        <td>{s.name}</td>
                        <td>
                            {user.role === "STAFF" ? (
                                <input 
                                  type="number" 
                                  title="Enter Marks"
                                  placeholder="0-100"
                                  style={{ width: '80px', padding: '0.4rem', border: '1px solid #ccc', borderRadius: '4px' }}
                                  value={staffInputs[s.id] || ""}
                                  onChange={(e) => setStaffInputs({...staffInputs, [s.id]: parseInt(e.target.value)})}
                                />
                            ) : (
                                <b>{examType === "CIA1" ? s.cia1 : examType === "CIA2" ? s.cia2 : s.model}</b>
                            )}
                        </td>
                        <td className={
                           (examType === "CIA1" && s.cia1 >= 30) || 
                           (examType === "CIA2" && s.cia2 >= 30) ||
                           (examType === "MODEL" && s.model >= 45) ? "status-pass" : "status-fail"
                        }>
                            {user.role === "STAFF" ? 
                                `Limit: ${examType === "MODEL" ? 100 : 60}` : 
                                ((examType === "CIA1" && s.cia1 >= 30) || 
                                 (examType === "CIA2" && s.cia2 >= 30) ||
                                 (examType === "MODEL" && s.model >= 45) ? "PASS" : "FAIL")}
                        </td>
                    </tr>
                  ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
