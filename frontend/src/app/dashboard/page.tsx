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
    const subject = subjects.find(s => s.staff && s.staff.id === user.id);
    if (!subject) {
      alert("You are not assigned to any subject.");
      return;
    }

    const payload = Object.keys(staffMarkInputs).map(studentIdStr => {
      const markValue = staffMarkInputs[studentIdStr];
      // Validation on Frontend too
      const maxMark = (examType === 'MODEL') ? 100 : 60;
      if (markValue > maxMark) {
        throw new Error(`Marks for exam ${examType} cannot exceed ${maxMark}`);
      }
      return {
        studentId: parseInt(studentIdStr),
        subjectId: subject.id,
        examType: examType,
        marks: markValue
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
      } else {
        const errorText = await res.text();
        alert("Error: " + errorText);
      }
    } catch (e: any) {
      alert(e.message || "Failed to save marks.");
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

  // Role Filtering for Data Visibility
  let filteredStudents = students;
  if (user.role === 'CC') {
    // Current Coordinator only sees their class (simulated with filtering first 15 for demo if real mapping not present)
    // In a real system, you'd filter by user.classID or similar.
    // For now, assume all demo students are under CC.
  }
  if (user.role === 'STUDENT') {
    filteredStudents = students.filter(s => s.id === user.id);
  }

  // Process and sort rankings for HOD / CC / STAFF / STUDENT (All students)
  let allProcessed = students.map((std) => {
    const stdMarks = marks.filter(m => m.student.id === std.id && m.examType === examType);
    const total = stdMarks.reduce((acc, curr) => acc + curr.marks, 0);
    const maxPossible = examType === 'MODEL' ? 100 : 60;
    const passThreshold = maxPossible / 2;
    const isFail = stdMarks.length > 0 && stdMarks.some(m => m.marks < passThreshold);
    return { ...std, total, isFail, marksCount: stdMarks.length };
  });

  // Calculate Rankings across ALL students
  allProcessed.sort((a, b) => b.total - a.total);

  // Statistics across the whole set
  let totalAcrossClass = 0;
  let studentsWithMarks = 0;
  let passCount = 0;

  allProcessed.forEach(s => {
    if (s.marksCount > 0) {
      totalAcrossClass += s.total;
      studentsWithMarks++;
      if (!s.isFail) passCount++;
    }
  });

  const classAverage = studentsWithMarks > 0 ? (totalAcrossClass / studentsWithMarks).toFixed(1) : "0";
  const passPercentage = studentsWithMarks > 0 ? ((passCount / studentsWithMarks) * 100).toFixed(1) : "0";

  const getRankBadge = (index: number) => {
    if (index === 0) return "🥇 Gold";
    if (index === 1) return "🥈 Silver";
    if (index === 2) return "🥉 Bronze";
    return index + 1;
  };

  // Visibility logic for the list displayed
  const displayList = user.role === 'STUDENT' ? allProcessed.filter(s => s.id === user.id) : allProcessed;

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

        <div className="nav-item" onClick={() => router.push("/dashboard/profile")}>
          Profile
        </div>

        <div style={{ marginTop: 'auto' }}>
          <div className="nav-item" onClick={handleLogout} style={{ color: 'var(--error)' }}>
            Logout
          </div>
        </div>
      </div>

      <div className="main-content">
        <div className="topbar">
          <h2 style={{ margin: 0, color: 'var(--primary-color)' }}>{examType} Dashboard</h2>
          
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <div className="top-tabs">
              <button className={`tab-btn ${examType === 'CIA1' ? 'active' : ''}`} onClick={() => setExamType('CIA1')}>CIA1</button>
              <button className={`tab-btn ${examType === 'CIA2' ? 'active' : ''}`} onClick={() => setExamType('CIA2')}>CIA2</button>
              <button className={`tab-btn ${examType === 'MODEL' ? 'active' : ''}`} onClick={() => setExamType('MODEL')}>Model</button>
            </div>

            <div className="profile-card" style={{ borderLeft: '1px solid #e2e8f0', paddingLeft: '1.5rem', cursor: 'pointer' }} onClick={() => router.push("/dashboard/profile")}>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontWeight: 600, fontSize: '0.95rem' }}>{user.name} ({user.role})</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-light)', textTransform: 'uppercase' }}>
                  {user.department}
                </div>
              </div>
              <div className="avatar">
                {user.name.charAt(0)}
              </div>
            </div>
          </div>
        </div>

        <div className="stats-grid">
          <div className="stat-card">
            <div style={{ color: 'var(--text-light)', marginBottom: '0.5rem', fontSize: '0.85rem', fontWeight: 600 }}>
              {user.role === 'STUDENT' ? 'MY TOTAL MARKS' : 'CLASS AVERAGE'}
            </div>
            <div className="stat-value">
              {user.role === 'STUDENT' ? (allProcessed.find(s => s.id === user.id)?.total || 0) : classAverage}
              {user.role !== 'STUDENT' && '%'}
            </div>
          </div>

          <div className="stat-card">
            <div style={{ color: 'var(--text-light)', marginBottom: '0.5rem', fontSize: '0.85rem', fontWeight: 600 }}>
              {user.role === 'STUDENT' ? 'MY CURRENT RANK' : 'PASS PERCENTAGE'}
            </div>
            <div className="stat-value" style={{ color: 'var(--success)' }}>
              {user.role === 'STUDENT' ? (allProcessed.findIndex(s => s.id === user.id) + 1) : (passPercentage + '%')}
            </div>
          </div>

          {(user.role === 'HOD' || user.role === 'CC' || user.role === 'STAFF') && (
            <div className="stat-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
              <button className="btn-primary" onClick={downloadPdf}>
                Download Subject PDF
              </button>
            </div>
          )}

          {user.role === 'STAFF' && (
            <div className="stat-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
              <button className="btn-primary" onClick={submitMarks} style={{ backgroundColor: 'var(--warning)', color: 'white' }}>
                Update & Save Database
              </button>
            </div>
          )}
        </div>

        <div style={{ backgroundColor: 'var(--card-bg)', padding: '1.5rem', borderRadius: '0.75rem', overflowX: 'auto' }}>
          <h3 style={{ marginTop: 0, marginBottom: '1.25rem', borderBottom: '1px solid #f1f5f9', paddingBottom: '1rem' }}>
             {user.role === 'STAFF' ? 'Input Performance Data' : 'Live Academic Rankings'}
          </h3>
          
          <table style={{ minWidth: '700px' }}>
            <thead>
              <tr>
                <th>Rank</th>
                <th>Register No.</th>
                <th>Name</th>
                {user.role === 'STAFF' ? <th>Marks (Max: {examType === 'MODEL' ? '100' : '60'})</th> : <th>Marks Scored</th>}
                {user.role !== 'STAFF' && <th>Pass Status</th>}
              </tr>
            </thead>
            <tbody>
              {displayList.map((student, idx) => {
                const globalIdx = allProcessed.findIndex(s => s.id === student.id);
                const isMe = user.role === 'STUDENT';
                
                return (
                  <tr key={student.id} className={(user.role !== 'STAFF' && student.isFail) ? 'highlight-fail' : (user.id === student.id && user.role === 'STUDENT') ? 'highlight-student' : ''}>
                    <td style={{ fontWeight: globalIdx <= 2 ? 600 : 400 }}>
                      {getRankBadge(globalIdx)}
                    </td>
                    <td>{student.username}</td>
                    <td>{student.name} {user.id === student.id && "(You)"}</td>
                    
                    {user.role === 'STAFF' ? (
                      <td>
                        <input 
                          type="number" 
                          min="0" max={examType === 'MODEL' ? 100 : 60}
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
                        {student.total > 0 ? (student.isFail ? "FAIL" : "PASS") : "NO DATA"}
                      </td>
                    )}
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
