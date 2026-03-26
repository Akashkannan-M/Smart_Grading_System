"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function DashboardPage() {
  const [user, setUser] = useState<any>(null);
  const [examType, setExamType] = useState("CIA1");
  const [year, setYear] = useState("1st Year");
  
  // Data States
  const [students, setStudents] = useState<any[]>([]);
  const [subjects, setSubjects] = useState<any[]>([]);
  const [marks, setMarks] = useState<any[]>([]);
  const [staffMarkInputs, setStaffMarkInputs] = useState<Record<string, number>>({});
  const [isClient, setIsClient] = useState(false);

  const router = useRouter();

  useEffect(() => {
    setIsClient(true);
    const usr = localStorage.getItem("user");
    if (!usr) {
      router.push("/login");
    } else {
      setUser(JSON.parse(usr));
    }
  }, [router]);

  useEffect(() => {
    if (user) {
      fetchData();
    }
  }, [user, examType]);

  const fetchData = async () => {
    try {
      const token = localStorage.getItem('token');
      const headers = { "Authorization": `Bearer ${token}` };

      // Fetch Subjects
      const subRes = await fetch("http://localhost:8080/api/marks/subjects", { headers });
      if (subRes.ok) setSubjects(await subRes.json());

      // Fetch Students
      const stdRes = await fetch("http://localhost:8080/api/marks/students", { headers });
      if (stdRes.ok) setStudents(await stdRes.json());

      // Fetch Marks dynamically based on Role
      if (user.role === 'STUDENT') {
        const mrkRes = await fetch(`http://localhost:8080/api/marks/student/${user.id}`, { headers });
        if (mrkRes.ok) setMarks(await mrkRes.json());
      } else {
        const mrkRes = await fetch("http://localhost:8080/api/marks/all", { headers });
        if (mrkRes.ok) setMarks(await mrkRes.json());
      }
    } catch(e) {
      console.error(e);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    router.push("/login");
  };

  const submitMarks = async () => {
    const payload = Object.keys(staffMarkInputs).map(studentIdStr => {
      // Find the subject assigned to this staff member
      const subject = subjects.find(s => s.staff && s.staff.id === user.id);
      return {
        studentId: parseInt(studentIdStr),
        subjectId: subject ? subject.id : 1, // Fallback strictly for demo if empty
        examType: examType,
        marks: staffMarkInputs[studentIdStr]
      };
    });

    try {
      const res = await fetch("http://localhost:8080/api/marks", {
        method: "POST",
        headers: { 
          "Content-Type": "application/json",
          "Authorization": `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify(payload)
      });
      if(res.ok) {
        alert("Marks saved successfully!");
        fetchData();
      }
    } catch (e) {
      alert("Failed to save marks.");
    }
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
  
  if (!isClient || !user) return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading Dashboard...</div>;

  // Process and sort rankings for HOD / CC / STAFF
  let processedRankings = students.map((std) => {
    // Filter marks for this student and examType
    const stdMarks = marks.filter(m => m.student.id === std.id && m.examType === examType);
    const total = stdMarks.reduce((acc, curr) => acc + curr.marks, 0);
    const isFail = stdMarks.some(m => m.marks < 50) || (stdMarks.length > 0 && total < 50);
    return { ...std, total, isFail, marksArr: stdMarks };
  });

  // Sort Highest Total First
  processedRankings.sort((a, b) => b.total - a.total);

  const getRankBadge = (index: number) => {
    if (index === 0) return "🥇 Gold";
    if (index === 1) return "🥈 Silver";
    if (index === 2) return "🥉 Bronze";
    return index + 1;
  };

  return (
    <div className="dashboard-container">
      <div className="sidebar" style={{ minWidth: '220px' }}>
        <div className="sidebar-title" style={{ fontSize: '1.3rem' }}>Smart Grading</div>
        
        {user.role === 'HOD' && (
          <div style={{ marginBottom: '2rem' }}>
            <label style={{ fontSize: '0.8rem', color: 'var(--text-light)', fontWeight: 600 }}>Filter Year:</label>
            <select 
              value={year} 
              onChange={(e) => setYear(e.target.value)}
              className="input-field"
              style={{ marginTop: '0.3rem' }}
            >
              <option>1st Year</option>
              <option>2nd Year</option>
              <option>3rd Year</option>
              <option>4th Year</option>
            </select>
          </div>
        )}

        <div style={{ marginTop: 'auto' }}>
          <div className="nav-item" onClick={handleLogout} style={{ color: 'var(--error)' }}>
            Logout
          </div>
        </div>
      </div>

      <div className="main-content">
        <div className="topbar">
          <h2 style={{ margin: 0, color: 'var(--primary-color)' }}>{examType} Overview</h2>
          
          <div style={{ display: 'flex', alignItems: 'center' }}>
            {/* Exam Tabs mapped to exactly the left side of the profile */}
            <div className="top-tabs">
              <button className={`tab-btn ${examType === 'CIA1' ? 'active' : ''}`} onClick={() => setExamType('CIA1')}>CIA1</button>
              <button className={`tab-btn ${examType === 'CIA2' ? 'active' : ''}`} onClick={() => setExamType('CIA2')}>CIA2</button>
              <button className={`tab-btn ${examType === 'MODEL' ? 'active' : ''}`} onClick={() => setExamType('MODEL')}>Model</button>
            </div>

            {/* Profile Card mapping at Top Right */}
            <div className="profile-card" style={{ borderLeft: '1px solid #e2e8f0', paddingLeft: '1.5rem' }}>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontWeight: 600, fontSize: '0.95rem' }}>{user.name} ({user.username})</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-light)', textTransform: 'uppercase' }}>
                  {user.role} - {user.department}
                </div>
              </div>
              <div className="avatar">
                {user.name.charAt(0)}
              </div>
            </div>
          </div>
        </div>

        <div className="stats-grid">
          {(user.role === 'HOD' || user.role === 'CC' || user.role === 'STAFF') && (
            <>
              <div className="stat-card">
                <div style={{ color: 'var(--text-light)', marginBottom: '0.5rem', fontSize: '0.85rem', fontWeight: 600 }}>CLASS AVERAGE</div>
                <div className="stat-value">75%</div>
              </div>
              <div className="stat-card">
                <div style={{ color: 'var(--text-light)', marginBottom: '0.5rem', fontSize: '0.85rem', fontWeight: 600 }}>PASS PERCENTAGE</div>
                <div className="stat-value" style={{ color: 'var(--success)' }}>88%</div>
              </div>
            </>
          )}

          {user.role === 'STUDENT' && (
             <div className="stat-card">
              <div style={{ color: 'var(--text-light)', marginBottom: '0.5rem', fontSize: '0.85rem', fontWeight: 600 }}>STUDENT AVG</div>
              <div className="stat-value">{(processedRankings.find(s => s.id === user.id)?.total || 0) > 0 ? '78%' : 'N/A'}</div>
            </div>
          )}

          {(user.role === 'HOD' || user.role === 'CC' || user.role === 'STUDENT') && (
            <div className="stat-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
              <button className="btn-primary" onClick={downloadPdf}>
                Download PDF Results
              </button>
            </div>
          )}

          {user.role === 'STAFF' && (
            <div className="stat-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
              <button className="btn-primary" onClick={submitMarks} style={{ backgroundColor: 'var(--warning)', color: 'white' }}>
                Save Marks to Database
              </button>
            </div>
          )}
        </div>

        <div style={{ backgroundColor: 'var(--card-bg)', padding: '1.5rem', borderRadius: '0.75rem', overflowX: 'auto' }}>
          <h3 style={{ marginTop: 0, marginBottom: '1.25rem', borderBottom: '1px solid #f1f5f9', paddingBottom: '1rem' }}>
             {user.role === 'STAFF' ? 'Enter Subject Marks' : 'Official Class Rankings'}
          </h3>
          
          <table style={{ minWidth: '700px' }}>
            <thead>
              <tr>
                <th>Rank</th>
                <th>Reg No.</th>
                <th>Name</th>
                {user.role === 'STAFF' ? <th>Marks Input (Range 0-100)</th> : <th>Total Marks</th>}
                {user.role !== 'STAFF' && <th>Status</th>}
              </tr>
            </thead>
            <tbody>
              {processedRankings.map((student, idx) => {
                const isMe = user.role === 'STUDENT' && user.id === student.id;
                
                return (
                  <tr key={student.id} className={user.role !== 'STAFF' && student.isFail ? 'highlight-fail' : isMe ? 'highlight-student' : ''}>
                    <td style={{ fontSize: idx <= 2 ? '1.15rem' : '1rem', fontWeight: idx <= 2 ? 600 : 400 }}>
                      {getRankBadge(idx)}
                    </td>
                    <td>{student.username}</td>
                    <td style={{ fontWeight: isMe ? 700 : 400 }}>{student.name} {isMe && "(You)"}</td>
                    
                    {user.role === 'STAFF' ? (
                      <td>
                        <input 
                          type="number" 
                          min="0" max="100"
                          title="Marks"
                          style={{ width: '80px', padding: '0.4rem', border: '1px solid #ccc', borderRadius: '4px' }}
                          value={staffMarkInputs[student.id] || ''}
                          onChange={(e) => setStaffMarkInputs({...staffMarkInputs, [student.id]: parseInt(e.target.value)})}
                        />
                      </td>
                    ) : (
                      <td style={{ fontWeight: 600 }}>{student.total > 0 ? student.total : 'N/A'}</td>
                    )}

                    {user.role !== 'STAFF' && (
                      <td className={student.isFail ? "status-fail" : "status-pass"}>
                        {student.total > 0 ? (student.isFail ? "FAIL" : "PASS") : "-"}
                      </td>
                    )}
                  </tr>
                )
              })}
              
              {students.length === 0 && (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-light)' }}>
                    No students populated. Please run backend /api/setup endpoint.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
