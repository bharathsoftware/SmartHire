import React, { useEffect, useState } from "react";
import {
  Link,
  Navigate,
  Route,
  Routes,
  useNavigate,
  useParams
} from "react-router-dom";
import api from "./api";

const getUser = () => {
  try {
    return JSON.parse(
      localStorage.getItem("smarthire_user") || "null"
    );
  } catch {
    return null;
  }
};

/* =========================================================
   LAYOUT
========================================================= */

function Layout({ children }) {
  const navigate = useNavigate();
  const [user, setUser] = useState(getUser());

  useEffect(() => {
    const sync = () => setUser(getUser());

    window.addEventListener("smarthire-auth", sync);

    return () => {
      window.removeEventListener("smarthire-auth", sync);
    };
  }, []);

  const logout = () => {
    localStorage.removeItem("smarthire_token");
    localStorage.removeItem("smarthire_user");

    window.dispatchEvent(
      new Event("smarthire-auth")
    );

    navigate("/login");
  };

  return (
    <>
      <header className="navbar">

        <Link className="brand" to="/">
          Smart<span>Hire</span>
        </Link>

        <nav>

          {user?.role === "JOB_SEEKER" && (
            <Link to="/jobs">
              Find Jobs
            </Link>
          )}

          {user?.role === "JOB_SEEKER" && (
            <Link to="/seeker">
              Dashboard
            </Link>
          )}

          {user?.role === "RECRUITER" && (
            <Link to="/recruiter">
              Recruiter
            </Link>
          )}

          {user?.role === "ADMIN" && (
            <Link to="/admin">
              Admin
            </Link>
          )}

          {!user ? (
            <>
              <Link to="/login">
                Login
              </Link>

              <Link
                className="nav-btn"
                to="/register"
              >
                Get Started
              </Link>
            </>
          ) : (
            <button
              className="link-button"
              onClick={logout}
            >
              Logout
            </button>
          )}

        </nav>

      </header>

      <main>
        {children}
      </main>

      <footer>
        © 2026 SmartHire · AI-Powered Recruitment & Job Portal
      </footer>
    </>
  );
}

/* =========================================================
   HOME
========================================================= */

function Home() {
  return (
    <section className="hero">

      <div className="hero-inner">

        <div className="pill">
          🚀 Smart recruitment made simple
        </div>

        <h1>
          Find the right job.
          <br />
          <span>
            Build your future.
          </span>
        </h1>

        <p>
          Connect talented people with great opportunities
          through SmartHire, a modern recruitment platform
          powered by secure Spring Boot APIs.
        </p>

        <div className="hero-actions">

          <Link
            className="primary-btn"
            to="/jobs"
          >
            Explore Jobs →
          </Link>

          <Link
            className="secondary-btn"
            to="/register"
          >
            Create Account
          </Link>

        </div>

        <div className="stats-row">

          <div>
            <strong>Java</strong>
            <small>
              Spring Boot Backend
            </small>
          </div>

          <div>
            <strong>JWT</strong>
            <small>
              Secure Authentication
            </small>
          </div>

          <div>
            <strong>React</strong>
            <small>
              Modern Frontend
            </small>
          </div>

          <div>
            <strong>MySQL</strong>
            <small>
              Reliable Database
            </small>
          </div>

        </div>

      </div>

    </section>
  );
}

/* =========================================================
   LOGIN
========================================================= */

function Login() {

  const navigate = useNavigate();

  const [form, setForm] = useState({
    email: "",
    password: ""
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {

    e.preventDefault();

    setError("");
    setLoading(true);

    try {

      const loginPayload = {
        email: form.email.trim(),
        password: form.password
      };

      const res = await api.post(
        "/users/login",
        loginPayload
      );

      localStorage.setItem(
        "smarthire_token",
        res.data.token
      );

      const me = await api.get("/users/me");

      localStorage.setItem(
        "smarthire_user",
        JSON.stringify(me.data)
      );

      window.dispatchEvent(
        new Event("smarthire-auth")
      );

      const role = me.data.role;

      if (role === "ADMIN") {
        navigate("/admin");
      } else if (role === "RECRUITER") {
        navigate("/recruiter");
      } else {
        navigate("/seeker");
      }

    } catch (err) {

      console.error(
        "Login error:",
        err.response?.data || err
      );

      setError(
        err.response?.data?.message ||
        "Login failed. Check your email and password."
      );

    } finally {

      setLoading(false);

    }
  };

  return (
    <AuthCard
      title="Welcome back"
      subtitle="Sign in to continue to SmartHire"
    >

      <form onSubmit={submit}>

        <label>
          Email
        </label>

        <input
          type="email"
          required
          value={form.email}
          onChange={(e) =>
            setForm({
              ...form,
              email: e.target.value
            })
          }
          placeholder="you@example.com"
        />

        <label>
          Password
        </label>

        <input
          type="password"
          required
          value={form.password}
          onChange={(e) =>
            setForm({
              ...form,
              password: e.target.value
            })
          }
          placeholder="••••••••"
        />

        {error && (
          <div className="error">
            {error}
          </div>
        )}

        <button
          className="primary-btn full"
          disabled={loading}
        >
          {loading
            ? "Signing in..."
            : "Sign In"}
        </button>

      </form>

      <p className="auth-switch">

        Don't have an account?{" "}

        <Link to="/register">
          Create one
        </Link>

      </p>

    </AuthCard>
  );
}

/* =========================================================
   REGISTER
========================================================= */

function Register() {

  const navigate = useNavigate();

  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
    role: "JOB_SEEKER",
    phone: "",
    location: ""
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {

    e.preventDefault();

    setError("");
    setLoading(true);

    const payload = {
      fullName: form.fullName.trim(),
      email: form.email.trim(),
      password: form.password,
      role: form.role,
      phone: form.phone.trim(),
      location: form.location.trim()
    };

    if (!payload.fullName) {
      setError(
        "Full name is required."
      );
      setLoading(false);
      return;
    }

    if (!payload.email) {
      setError(
        "Email is required."
      );
      setLoading(false);
      return;
    }

    if (!payload.password) {
      setError(
        "Password is required."
      );
      setLoading(false);
      return;
    }

    try {

      const response = await api.post(
        "/users",
        payload
      );

      console.log(
        "Registration successful:",
        response.data
      );

      alert(
        "Account created successfully! Please login."
      );

      navigate("/login");

    } catch (err) {

      console.error(
        "Registration error:",
        err.response?.data || err
      );

      setError(
        err.response?.data?.message ||
        "Registration failed. Please try again."
      );

    } finally {

      setLoading(false);

    }
  };

  return (
    <AuthCard
      title="Create your account"
      subtitle="Join SmartHire today"
    >

      <form onSubmit={submit}>

        <label>
          Full Name
        </label>

        <input
          type="text"
          required
          value={form.fullName}
          onChange={(e) =>
            setForm({
              ...form,
              fullName: e.target.value
            })
          }
          placeholder="Your full name"
        />

        <label>
          Email
        </label>

        <input
          type="email"
          required
          value={form.email}
          onChange={(e) =>
            setForm({
              ...form,
              email: e.target.value
            })
          }
          placeholder="you@example.com"
        />

        <label>
          Password
        </label>

        <input
          type="password"
          required
          value={form.password}
          onChange={(e) =>
            setForm({
              ...form,
              password: e.target.value
            })
          }
          placeholder="Create a password"
        />

        <div className="two">

          <div>

            <label>
              Role
            </label>

            <select
              value={form.role}
              onChange={(e) =>
                setForm({
                  ...form,
                  role: e.target.value
                })
              }
            >

              <option value="JOB_SEEKER">
                Job Seeker
              </option>

              <option value="RECRUITER">
                Recruiter
              </option>

            </select>

          </div>

          <div>

            <label>
              Phone
            </label>

            <input
              type="tel"
              value={form.phone}
              onChange={(e) =>
                setForm({
                  ...form,
                  phone: e.target.value
                })
              }
              placeholder="Phone"
            />

          </div>

        </div>

        <label>
          Location
        </label>

        <input
          type="text"
          value={form.location}
          onChange={(e) =>
            setForm({
              ...form,
              location: e.target.value
            })
          }
          placeholder="Coimbatore"
        />

        {error && (
          <div className="error">
            {error}
          </div>
        )}

        <button
          type="submit"
          className="primary-btn full"
          disabled={loading}
        >
          {loading
            ? "Creating..."
            : "Create Account"}
        </button>

      </form>

      <p className="auth-switch">

        Already registered?{" "}

        <Link to="/login">
          Sign in
        </Link>

      </p>

    </AuthCard>
  );
}

/* =========================================================
   AUTH CARD
========================================================= */

function AuthCard({
  title,
  subtitle,
  children
}) {

  return (
    <div className="auth-page">

      <div className="auth-card">

        <Link
          className="brand center"
          to="/"
        >
          Smart<span>Hire</span>
        </Link>

        <h2>
          {title}
        </h2>

        <p>
          {subtitle}
        </p>

        {children}

      </div>

    </div>
  );
}

/* =========================================================
   JOBS
========================================================= */

function Jobs() {

  const [jobs, setJobs] = useState([]);

  const [q, setQ] = useState("");
  const [location, setLocation] = useState("");
  const [type, setType] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = async () => {

    setLoading(true);
    setError("");

    try {

      let res;

      if (q) {

        res = await api.get(
          "/job-seekers/jobs/search",
          {
            params: {
              title: q
            }
          }
        );

      } else if (location) {

        res = await api.get(
          "/job-seekers/jobs/location",
          {
            params: {
              location
            }
          }
        );

      } else if (type) {

        res = await api.get(
          "/job-seekers/jobs/type",
          {
            params: {
              jobType: type
            }
          }
        );

      } else {

        res = await api.get(
          "/job-seekers/jobs"
        );

      }

      setJobs(res.data);

    } catch (err) {

      console.error(
        "Jobs error:",
        err.response?.data || err
      );

      setError(
        "Please login as a Job Seeker to view jobs."
      );

    } finally {

      setLoading(false);

    }
  };

  useEffect(() => {
    load();
  }, []);

  const clear = () => {

    setQ("");
    setLocation("");
    setType("");

    setTimeout(() => {
      load();
    }, 0);
  };

  return (
    <div className="page">

      <div className="page-head">

        <div>

          <div className="pill">
            💼 Opportunities
          </div>

          <h2>
            Find your next opportunity
          </h2>

          <p>
            Search jobs from SmartHire employers.
          </p>

        </div>

      </div>

      <div className="searchbar">

        <input
          value={q}
          onChange={(e) =>
            setQ(e.target.value)
          }
          placeholder="Search by job title..."
        />

        <input
          value={location}
          onChange={(e) =>
            setLocation(e.target.value)
          }
          placeholder="Location..."
        />

        <select
          value={type}
          onChange={(e) =>
            setType(e.target.value)
          }
        >

          <option value="">
            All types
          </option>

          <option value="FULL_TIME">
            Full Time
          </option>

          <option value="PART_TIME">
            Part Time
          </option>

          <option value="CONTRACT">
            Contract
          </option>

        </select>

        <button
          className="primary-btn"
          onClick={load}
        >
          Search
        </button>

        <button
          className="ghost-btn"
          onClick={clear}
        >
          Clear
        </button>

      </div>

      {error && (
        <div className="error">
          {error}
        </div>
      )}

      {loading ? (

        <div className="empty">
          Loading jobs...
        </div>

      ) : jobs.length === 0 ? (

        <div className="empty">
          No jobs found.
        </div>

      ) : (

        <div className="job-grid">

          {jobs.map((job) => (

            <JobCard
              key={job.id}
              job={job}
            />

          ))}

        </div>

      )}

    </div>
  );
}

/* =========================================================
   JOB CARD
========================================================= */

function JobCard({ job }) {

  const navigate = useNavigate();

  return (
    <article className="job-card">

      <div className="job-top">

        <div className="company-logo">
          {job.companyName?.charAt(0) || "S"}
        </div>

        <span className="tag">
          {job.jobType}
        </span>

      </div>

      <h3>
        {job.title}
      </h3>

      <p className="company">
        {job.companyName}
      </p>

      <div className="meta">
        📍 {job.location || "Remote"}
        {" · "}
        🎓 {job.experienceLevel || "Any"}
      </div>

      <p className="skills">
        {job.skills}
      </p>

      <div className="job-bottom">

        <strong>
          ₹
          {Number(
            job.salary || 0
          ).toLocaleString("en-IN")}
          /year
        </strong>

        <button
          className="small-btn"
          onClick={() =>
            navigate(`/jobs/${job.id}`)
          }
        >
          View Details
        </button>

      </div>

    </article>
  );
}

/* =========================================================
   JOB DETAILS
========================================================= */

function JobDetails() {

  const { id } = useParams();

  const [job, setJob] = useState(null);
  const [msg, setMsg] = useState("");
  const [loading, setLoading] = useState(true);
  const [degree, setDegree] = useState("");
  const [resumeFile, setResumeFile] = useState(null);
  const [applying, setApplying] = useState(false);

  useEffect(() => {

    const loadJob = async () => {

      try {

        const response = await api.get(
          "/job-seekers/jobs"
        );

        const foundJob =
          response.data.find(
            (item) =>
              String(item.id) === String(id)
          );

        setJob(
          foundJob || null
        );

      } catch (err) {

        console.error(
          "Job details error:",
          err.response?.data || err
        );

      } finally {

        setLoading(false);

      }
    };

    loadJob();

  }, [id]);

  const apply = async () => {

    try {
      setApplying(true);
      setMsg("");
      if (!degree.trim()) {
        setMsg("Please enter your degree or qualification.");
        return;
      }
      if (!resumeFile) {
        setMsg("Please upload your resume as a PDF.");
        return;
      }
      if (resumeFile.type !== "application/pdf" && !resumeFile.name.toLowerCase().endsWith(".pdf")) {
        setMsg("Only PDF resumes are allowed.");
        return;
      }
      const form = new FormData();
      form.append("file", resumeFile);
      form.append("degree", degree.trim());
      await api.post("/job-seeker/resume/upload", form);
      await api.post(`/job-seekers/apply/${id}`);
      setMsg("Application submitted successfully! Your PDF resume has been saved.");

    } catch (err) {

      console.error(
        "Application error:",
        err.response?.data || err
      );

      const data = err.response?.data;
      setMsg(typeof data === "string" ? data : (data?.message || "Please login as a Job Seeker or check if you already applied."));
    } finally {
      setApplying(false);
    }
  };

  if (loading) {

    return (
      <div className="empty">
        Loading...
      </div>
    );
  }

  if (!job) {

    return (
      <div className="empty">
        Job not found.
      </div>
    );
  }

  return (
    <div className="page narrow">

      <Link
        to="/jobs"
        className="back"
      >
        ← Back to jobs
      </Link>

      <article className="detail-card">

        <div className="company-logo big">
          {job.companyName?.charAt(0) || "S"}
        </div>

        <span className="tag">
          {job.jobType}
        </span>

        <h1>
          {job.title}
        </h1>

        <h3 className="company">
          {job.companyName}
        </h3>

        <div className="detail-meta">
          📍 {job.location}
          {" · "}
          🎓 {job.experienceLevel}
          {" · "}
          💰 ₹
          {Number(
            job.salary || 0
          ).toLocaleString("en-IN")}
          /year
        </div>

        <hr />

        <h3>
          Job Description
        </h3>

        <p>
          {job.description}
        </p>

        <h3>
          Required Skills
        </h3>

        <p>
          {job.skills}
        </p>

        {msg && (
          <div className="success">
            {msg}
          </div>
        )}

        <div className="application-form">
          <h3>Apply for this job</h3>
          <label htmlFor="degree">Degree / Qualification</label>
          <input id="degree" value={degree} onChange={(e) => setDegree(e.target.value)} placeholder="e.g. B.E. Computer Science" required />
          <label htmlFor="resume-pdf">Resume (PDF, max 5 MB)</label>
          <input id="resume-pdf" type="file" accept="application/pdf,.pdf" onChange={(e) => setResumeFile(e.target.files?.[0] || null)} required />
          <button className="primary-btn" onClick={apply} disabled={applying}>
            {applying ? "Submitting..." : "Apply for this job"}
          </button>
        </div>

      </article>

    </div>
  );
}

/* =========================================================
   PROTECTED ROUTE
========================================================= */

function Protected({
  roles,
  children
}) {

  const user = getUser();

  if (!user) {

    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  if (
    roles &&
    !roles.includes(user.role)
  ) {

    return (
      <Navigate
        to="/"
        replace
      />
    );
  }

  return children;
}

/* =========================================================
   JOB SEEKER DASHBOARD
========================================================= */

function SeekerDashboard() {

  const [apps, setApps] = useState([]);
  const [profile, setProfile] = useState(null);
  const [resume, setResume] = useState(null);
  const [error, setError] = useState("");

  const load = async () => {

    try {

      const [
        applications,
        profileData,
        resumeData
      ] = await Promise.allSettled([

        api.get(
          "/applications/my-applications"
        ),

        api.get(
          "/job-seeker/profile"
        ),

        api.get(
          "/job-seeker/resume"
        )

      ]);

      if (
        applications.status ===
        "fulfilled"
      ) {

        setApps(
          applications.value.data
        );
      }

      if (
        profileData.status ===
        "fulfilled"
      ) {

        setProfile(
          profileData.value.data
        );
      }

      if (
        resumeData.status ===
        "fulfilled"
      ) {

        setResume(
          resumeData.value.data
        );
      }

    } catch (err) {

      console.error(err);

      setError(
        "Could not load dashboard."
      );
    }
  };

  useEffect(() => {
    load();
  }, []);

  return (
    <div className="page">

      <div className="page-head">

        <div>

          <div className="pill">
            👋 Job Seeker
          </div>

          <h2>
            Welcome, {getUser()?.fullName}
          </h2>

          <p>
            Track applications and manage
            your career profile.
          </p>

        </div>

        <Link
          className="primary-btn"
          to="/jobs"
        >
          Find Jobs
        </Link>

      </div>

      {error && (
        <div className="error">
          {error}
        </div>
      )}

      <div className="dashboard-grid">

        <div className="dash-stat">

          <span>
            Applications
          </span>

          <strong>
            {apps.length}
          </strong>

        </div>

        <div className="dash-stat">

          <span>
            Profile
          </span>

          <strong>
            {profile ? "✓" : "—"}
          </strong>

        </div>

        <div className="dash-stat">

          <span>
            Resume
          </span>

          <strong>
            {resume ? "✓" : "—"}
          </strong>

        </div>

      </div>

      <section className="panel">

        <h3>
          My Applications
        </h3>

        {apps.length === 0 ? (

          <p>
            No applications yet.
          </p>

        ) : (

          <div className="table-wrap">

            <table>

              <thead>

                <tr>
                  <th>Job</th>
                  <th>Company</th>
                  <th>Status</th>
                  <th>Applied</th>
                </tr>

              </thead>

              <tbody>

                {apps.map((app) => (

                  <tr key={app.id}>

                    <td>
                      {app.jobTitle}
                    </td>

                    <td>
                      {app.companyName}
                    </td>

                    <td>

                      <span
                        className={`status ${app.status?.toLowerCase()}`}
                      >
                        {app.status}
                      </span>

                    </td>

                    <td>

                      {app.appliedAt
                        ? new Date(
                            app.appliedAt
                          ).toLocaleDateString()
                        : "—"}

                    </td>

                  </tr>

                ))}

              </tbody>

            </table>

          </div>

        )}

      </section>

    </div>
  );
}

/* =========================================================
   RECRUITER DASHBOARD
========================================================= */

function RecruiterDashboard() {

  const [jobs, setJobs] = useState([]);
  const [apps, setApps] = useState([]);

  const [selectedJob, setSelectedJob] =
    useState(null);

  const [title, setTitle] =
    useState("");

  const [desc, setDesc] =
    useState("");

  const [error, setError] =
    useState("");

  const [msg, setMsg] =
    useState("");

  const [updating, setUpdating] =
    useState(null);
  const [publishing, setPublishing] = useState(false);

  /* =======================================================
     LOAD RECRUITER DATA
  ======================================================= */

  const load = async () => {

    try {

      setError("");

      const response = await api.get(
        "/recruiters/my-jobs"
      );

      setJobs(
        response.data
      );

      const results = await Promise.all(
        response.data.map((job) => api.get(`/recruiters/jobs/${job.id}/applications`))
      );
      setApps(results.flatMap((result) => result.data));

    } catch (err) {

      console.error(
        "Recruiter error:",
        err.response?.data || err
      );

      setError(
        err.response?.data?.message ||
        "Unable to load recruiter data."
      );
    }
  };

  useEffect(() => {
    load();
  }, []);

  /* =======================================================
     UPDATE APPLICATION STATUS
  ======================================================= */

  const updateStatus = async (
    applicationId,
    status
  ) => {

    try {

      setUpdating(
        applicationId
      );

      setError("");
      setMsg("");

      await api.put(
        `/applications/${applicationId}/status`,
        null,
        {
          params: {
            status: status
          }
        }
      );

      setMsg(
        `Application status updated to ${status}.`
      );

      await load();

    } catch (err) {

      console.error(
        "Status update error:",
        err.response?.data || err
      );

      setError(
        err.response?.data?.message ||
        "Could not update application status."
      );

    } finally {

      setUpdating(null);

    }
  };

  /* =======================================================
     CREATE JOB
  ======================================================= */

  const create = async (e) => {

    e.preventDefault();

    setError("");
    setMsg("");

    try {
      if (!title.trim() || !desc.trim()) {
        setError("Enter both a job title and description.");
        return;
      }
      setPublishing(true);
      const payload = {

        title:
          title.trim(),

        description:
          desc.trim(),

        companyName:
          "SmartHire Technologies",

        location:
          "Coimbatore",

        jobType:
          "FULL_TIME",

        experienceLevel:
          "ENTRY_LEVEL",

        skills:
          "Java, Spring Boot, MySQL, REST API",

        salary:
          600000,

        recruiter: {
          id: getUser().id
        }

      };

      await api.post(
        "/jobs",
        payload
      );

      setTitle("");
      setDesc("");

      setMsg(
        "Job posted successfully."
      );

      await load();

    } catch (err) {

      console.error(
        "Job posting error:",
        err.response?.data || err
      );

      const data = err.response?.data;
      setError(typeof data === "string" ? data : (data?.message || "Job posting failed. Check that the backend is running and you are logged in as a recruiter."));
    } finally {
      setPublishing(false);
    }
  };

  /* =======================================================
     OPEN APPLICANTS
  ======================================================= */

  const openApplicants = (job) => {

    setSelectedJob(job);

    setMsg("");
    setError("");
  };

  /* =======================================================
     FILTER SELECTED JOB APPLICATIONS
  ======================================================= */

  const selectedApplications =
    selectedJob
      ? apps.filter(
          (app) =>
            Number(app.jobId) ===
            Number(selectedJob.id)
        )
      : [];

  return (
    <div className="page">

      {/* ===================================================
          HEADER
      =================================================== */}

      <div className="page-head">

        <div>

          <div className="pill">
            🏢 Recruiter
          </div>

          <h2>
            Recruiter Dashboard
          </h2>

          <p>
            Manage your job postings and applicants.
          </p>

        </div>

      </div>

      {/* ===================================================
          MESSAGES
      =================================================== */}

      {error && (
        <div className="error">
          {error}
        </div>
      )}

      {msg && (
        <div className="success">
          {msg}
        </div>
      )}

      {/* ===================================================
          STATS
      =================================================== */}

      <div className="dashboard-grid">

        <div className="dash-stat">

          <span>
            My Jobs
          </span>

          <strong>
            {jobs.length}
          </strong>

        </div>

        <div className="dash-stat">

          <span>
            Applicants
          </span>

          <strong>
            {apps.length}
          </strong>

        </div>

      </div>

      {/* ===================================================
          POST JOB + MY JOBS
      =================================================== */}

      <div className="two-panels">

        {/* POST JOB */}

        <section className="panel">

          <h3>
            Post a Job
          </h3>

          <form onSubmit={create}>

            <label>
              Job Title
            </label>

            <input
              required
              value={title}
              onChange={(e) =>
                setTitle(
                  e.target.value
                )
              }
              placeholder="Java Developer"
            />

            <label>
              Description
            </label>

            <textarea
              required
              value={desc}
              onChange={(e) =>
                setDesc(
                  e.target.value
                )
              }
              placeholder="Describe the role..."
            />

            <button className="primary-btn" type="submit" disabled={publishing}>
              {publishing ? "Publishing..." : "Publish Job"}
            </button>

          </form>

        </section>

        {/* MY JOBS */}

        <section className="panel">

          <h3>
            My Jobs
          </h3>

          {jobs.length === 0 ? (

            <p>
              No jobs posted yet.
            </p>

          ) : (

            jobs.map((job) => {

              const applicantCount = Number.isFinite(Number(job.applicantCount))
                ? Number(job.applicantCount)
                : apps.filter((app) => Number(app.jobId) === Number(job.id)).length;

              return (

                <div
                  className="mini-row"
                  key={job.id}
                >

                  <div>

                    <strong>
                      {job.title}
                    </strong>

                    <small>
                      {job.location}
                      {" · "}
                      {job.jobType}
                    </small>

                  </div>

                  <div>

                    <span>
                      {applicantCount}{" "}
                      {applicantCount === 1
                        ? "applicant"
                        : "applicants"}
                    </span>

                    <br />

                    <button
                      className="small-btn"
                      onClick={() =>
                        openApplicants(job)
                      }
                    >
                      View Applicants
                    </button>

                  </div>

                </div>

              );

            })

          )}

        </section>

      </div>

      {/* ===================================================
          APPLICANTS
      =================================================== */}

      {selectedJob && (

        <section className="panel">

          <div
            style={{
              display: "flex",
              justifyContent:
                "space-between",
              alignItems:
                "center",
              gap: "15px"
            }}
          >

            <div>

              <h3>
                Applicants
              </h3>

              <p>
                {selectedJob.title}
              </p>

            </div>

            <button
              className="ghost-btn"
              onClick={() =>
                setSelectedJob(null)
              }
            >
              Close
            </button>

          </div>

          {selectedApplications.length === 0 ? (

            <div className="empty">
              No applicants for this job yet.
            </div>

          ) : (

            <div className="table-wrap">

              <table>

                <thead>

                  <tr>

                    <th>
                      Candidate
                    </th>

                    <th>Contact / Degree</th>
                    <th>Status</th>
                    <th>Applied</th>

                    <th>
                      Actions
                    </th>

                  </tr>

                </thead>

                <tbody>

                  {selectedApplications.map(
                    (app) => (

                      <tr key={app.id}>

                        {/* CANDIDATE */}

                        <td>

                          <strong>
                            {app.jobSeekerName}
                          </strong>

                          <br />

                          <small>{app.jobSeekerEmail || "Email unavailable"}</small>
                          <br />
                          <small>Degree: {app.degree || "Not provided"}</small>
                          <br />
                          <small>Application #{app.id}</small>
                          <br />
                          {app.resumeAvailable ? (
                            <button className="small-btn" onClick={async () => {
                              try {
                                const response = await api.get(`/recruiters/applications/${app.id}/resume`, { responseType: "blob" });
                                const url = URL.createObjectURL(response.data);
                                const anchor = document.createElement("a");
                                anchor.href = url;
                                anchor.download = app.resumeFileName || "resume.pdf";
                                document.body.appendChild(anchor);
                                anchor.click();
                                anchor.remove();
                                URL.revokeObjectURL(url);
                              } catch (e) { setError("Could not download this resume."); }
                            }}>Download PDF</button>
                          ) : <small>Resume not uploaded</small>}

                        </td>

                        {/* STATUS */}

                        <td>

                          <span
                            className={`status ${app.status?.toLowerCase()}`}
                          >
                            {app.status}
                          </span>

                        </td>

                        {/* APPLIED DATE */}

                        <td>

                          {app.appliedAt
                            ? new Date(
                                app.appliedAt
                              ).toLocaleDateString()
                            : "—"}

                        </td>

                        {/* ACTIONS */}

                        <td>

                          {/* ==========================
                              APPLIED
                          ========================== */}

                          {app.status ===
                            "APPLIED" && (

                            <div
                              style={{
                                display:
                                  "flex",
                                gap: "8px",
                                flexWrap:
                                  "wrap"
                              }}
                            >

                              <button
                                className="small-btn"
                                disabled={
                                  updating ===
                                  app.id
                                }
                                onClick={() =>
                                  updateStatus(
                                    app.id,
                                    "SHORTLISTED"
                                  )
                                }
                              >

                                {updating ===
                                app.id
                                  ? "Updating..."
                                  : "Shortlist"}

                              </button>

                              <button
                                className="ghost-btn"
                                disabled={
                                  updating ===
                                  app.id
                                }
                                onClick={() =>
                                  updateStatus(
                                    app.id,
                                    "REJECTED"
                                  )
                                }
                              >
                                Reject
                              </button>

                            </div>

                          )}

                          {/* ==========================
                              SHORTLISTED
                          ========================== */}

                          {app.status ===
                            "SHORTLISTED" && (

                            <div
                              style={{
                                display:
                                  "flex",
                                gap: "8px",
                                flexWrap:
                                  "wrap"
                              }}
                            >

                              <button
                                className="small-btn"
                                disabled={
                                  updating ===
                                  app.id
                                }
                                onClick={() =>
                                  updateStatus(
                                    app.id,
                                    "HIRED"
                                  )
                                }
                              >

                                {updating ===
                                app.id
                                  ? "Updating..."
                                  : "Hire"}

                              </button>

                              <button
                                className="ghost-btn"
                                disabled={
                                  updating ===
                                  app.id
                                }
                                onClick={() =>
                                  updateStatus(
                                    app.id,
                                    "REJECTED"
                                  )
                                }
                              >
                                Reject
                              </button>

                            </div>

                          )}

                          {/* ==========================
                              HIRED
                          ========================== */}

                          {app.status ===
                            "HIRED" && (

                            <span>
                              ✅ Candidate Hired
                            </span>

                          )}

                          {/* ==========================
                              REJECTED
                          ========================== */}

                          {app.status ===
                            "REJECTED" && (

                            <span>
                              ❌ Application Rejected
                            </span>

                          )}

                        </td>

                      </tr>

                    )
                  )}

                </tbody>

              </table>

            </div>

          )}

        </section>

      )}

    </div>
  );
}

/* =========================================================
   ADMIN DASHBOARD
========================================================= */

function AdminDashboard() {

  const [stats, setStats] =
    useState({});

  const [users, setUsers] =
    useState([]);

  const [jobs, setJobs] =
    useState([]);

  const [apps, setApps] =
    useState([]);

  const [error, setError] =
    useState("");

  const load = async () => {

    try {

      const [
        statsResponse,
        usersResponse,
        jobsResponse,
        appsResponse
      ] = await Promise.all([

        api.get(
          "/admin/dashboard/stats"
        ),

        api.get(
          "/admin/users"
        ),

        api.get(
          "/admin/jobs"
        ),

        api.get(
          "/admin/applications"
        )

      ]);

      setStats(
        statsResponse.data
      );

      setUsers(
        usersResponse.data
      );

      setJobs(
        jobsResponse.data
      );

      setApps(
        appsResponse.data
      );

    } catch (err) {

      console.error(
        "Admin error:",
        err.response?.data || err
      );

      setError(
        err.response?.data?.message ||
        "Admin data could not be loaded."
      );

    }
  };

  useEffect(() => {
    load();
  }, []);

  return (
    <div className="page">

      <div className="page-head">

        <div>

          <div className="pill">
            👑 Administration
          </div>

          <h2>
            Admin Dashboard
          </h2>

          <p>
            Monitor SmartHire platform activity.
          </p>

        </div>

      </div>

      {error && (
        <div className="error">
          {error}
        </div>
      )}

      {/* ===================================================
          STATISTICS
      =================================================== */}

      <div className="dashboard-grid six">

        {[
          [
            "Users",
            stats.totalUsers
          ],

          [
            "Job Seekers",
            stats.totalJobSeekers
          ],

          [
            "Recruiters",
            stats.totalRecruiters
          ],

          [
            "Admins",
            stats.totalAdmins
          ],

          [
            "Jobs",
            stats.totalJobs
          ],

          [
            "Applications",
            stats.totalApplications
          ]

        ].map(
          ([key, value]) => (

            <div
              className="dash-stat"
              key={key}
            >

              <span>
                {key}
              </span>

              <strong>
                {value ?? "—"}
              </strong>

            </div>

          )
        )}

      </div>

      {/* ===================================================
          USERS
      =================================================== */}

      <section className="panel">

        <h3>
          Users
        </h3>

        <div className="table-wrap">

          <table>

            <thead>

              <tr>

                <th>
                  Name
                </th>

                <th>
                  Email
                </th>

                <th>
                  Role
                </th>

                <th>
                  Location
                </th>

              </tr>

            </thead>

            <tbody>

              {users.map(
                (user) => (

                  <tr key={user.id}>

                    <td>
                      {user.fullName}
                    </td>

                    <td>
                      {user.email}
                    </td>

                    <td>

                      <span className="status">
                        {user.role}
                      </span>

                    </td>

                    <td>
                      {user.location}
                    </td>

                  </tr>

                )
              )}

            </tbody>

          </table>

        </div>

      </section>

      {/* ===================================================
          JOBS
      =================================================== */}

      <section className="panel">

        <h3>
          Jobs
        </h3>

        <div className="table-wrap">

          <table>

            <thead>

              <tr>

                <th>
                  Title
                </th>

                <th>
                  Company
                </th>

                <th>
                  Location
                </th>

                <th>
                  Salary
                </th>

              </tr>

            </thead>

            <tbody>

              {jobs.map(
                (job) => (

                  <tr key={job.id}>

                    <td>
                      {job.title}
                    </td>

                    <td>
                      {job.companyName}
                    </td>

                    <td>
                      {job.location}
                    </td>

                    <td>
                      ₹
                      {Number(
                        job.salary || 0
                      ).toLocaleString(
                        "en-IN"
                      )}
                    </td>

                  </tr>

                )
              )}

            </tbody>

          </table>

        </div>

      </section>

      {/* ===================================================
          APPLICATIONS
      =================================================== */}

      <section className="panel">

        <h3>
          Applications
        </h3>

        <div className="table-wrap">

          <table>

            <thead>

              <tr>

                <th>
                  Job
                </th>

                <th>
                  Candidate
                </th>

                <th>
                  Status
                </th>

                <th>
                  Applied
                </th>

              </tr>

            </thead>

            <tbody>

              {apps.map(
                (app) => (

                  <tr key={app.id}>

                    <td>
                      {app.jobTitle}
                    </td>

                    <td>
                      {app.jobSeekerName}
                    </td>

                    <td>

                      <span
                        className={`status ${app.status?.toLowerCase()}`}
                      >
                        {app.status}
                      </span>

                    </td>

                    <td>

                      {app.appliedAt
                        ? new Date(
                            app.appliedAt
                          ).toLocaleDateString()
                        : "—"}

                    </td>

                  </tr>

                )
              )}

            </tbody>

          </table>

        </div>

      </section>

    </div>
  );
}

/* =========================================================
   APP ROUTES
========================================================= */

export default function App() {

  return (
    <Layout>

      <Routes>

        {/* HOME */}

        <Route
          path="/"
          element={
            <Home />
          }
        />

        {/* LOGIN */}

        <Route
          path="/login"
          element={
            <Login />
          }
        />

        {/* REGISTER */}

        <Route
          path="/register"
          element={
            <Register />
          }
        />

        {/* JOBS */}

        <Route
          path="/jobs"
          element={
            <Protected
              roles={[
                "JOB_SEEKER"
              ]}
            >
              <Jobs />
            </Protected>
          }
        />

        {/* JOB DETAILS */}

        <Route
          path="/jobs/:id"
          element={
            <Protected
              roles={[
                "JOB_SEEKER"
              ]}
            >
              <JobDetails />
            </Protected>
          }
        />

        {/* JOB SEEKER */}

        <Route
          path="/seeker"
          element={
            <Protected
              roles={[
                "JOB_SEEKER"
              ]}
            >
              <SeekerDashboard />
            </Protected>
          }
        />

        {/* RECRUITER */}

        <Route
          path="/recruiter"
          element={
            <Protected
              roles={[
                "RECRUITER"
              ]}
            >
              <RecruiterDashboard />
            </Protected>
          }
        />

        {/* ADMIN */}

        <Route
          path="/admin"
          element={
            <Protected
              roles={[
                "ADMIN"
              ]}
            >
              <AdminDashboard />
            </Protected>
          }
        />

        {/* FALLBACK */}

        <Route
          path="*"
          element={
            <Navigate
              to="/"
              replace
            />
          }
        />

      </Routes>

    </Layout>
  );
}