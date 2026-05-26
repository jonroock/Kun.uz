import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
import ProfilePage from './pages/ProfilePage';
import ArticlesPage from './pages/ArticlePage';
import WriteArticlePage from './pages/WriteArticlePage';
import MyArticlesPage from './pages/MyArticlesPage';
import PublisherPage from './pages/PublisherPage';

function Navbar({ user, onLogout }) {
    const navigate = useNavigate();
    const location = useLocation();
    const isModerator = user.roleList && user.roleList.includes('ROLE_MODERATOR');
    const isPublisher = user.roleList && user.roleList.includes('ROLE_PUBLISH');

    const isActive = (path) => location.pathname === path;

    return (
        <div style={styles.navbar}>
            <h2 style={styles.logo} onClick={() => navigate('/')}>🗞️ Kun.uz</h2>
            <div style={styles.navLinks}>
                <button style={isActive('/') ? styles.activeLink : styles.navLink}
                        onClick={() => navigate('/')}>🏠 Home</button>
                <button style={isActive('/articles') ? styles.activeLink : styles.navLink}
                        onClick={() => navigate('/articles')}>📰 Articles</button>
                {isPublisher && (
                    <button style={isActive('/publisher') ? styles.activeLink : styles.navLink}
                            onClick={() => navigate('/publisher')}>📋 Publisher</button>
                )}
                {isModerator && (
                    <button style={isActive('/write') ? styles.activeLink : styles.navLink}
                            onClick={() => navigate('/write')}>✍️ Write</button>
                )}
                {isModerator && (
                    <button style={isActive('/my-articles') ? styles.activeLink : styles.navLink}
                            onClick={() => navigate('/my-articles')}>📝 My Articles</button>
                )}
                <button style={isActive('/profile') ? styles.activeLink : styles.navLink}
                        onClick={() => navigate('/profile')}>👤 Profile</button>
            </div>
            <div style={styles.userInfo}>
                <span style={styles.userName}>{user.name} {user.surname}</span>
                <button style={styles.logoutBtn} onClick={onLogout}>Logout</button>
            </div>
        </div>
    );
}

function AppContent() {
    const [user, setUser] = useState(
        JSON.parse(localStorage.getItem('user')) || null
    );
    const [authPage, setAuthPage] = useState('login');
    const navigate = useNavigate();

    const handleLogin = (userData) => {
        setUser(userData);
        navigate('/');
    };

    const handleLogout = () => {
        localStorage.removeItem('jwt');
        localStorage.removeItem('user');
        setUser(null);
        setAuthPage('login');
    };

    if (!user) {
        if (authPage === 'register') {
            return <RegisterPage onSwitch={() => setAuthPage('login')} />;
        }
        return <LoginPage onLogin={handleLogin} onSwitch={() => setAuthPage('register')} />;
    }

    const isModerator = user.roleList && user.roleList.includes('ROLE_MODERATOR');
    const isPublisher = user.roleList && user.roleList.includes('ROLE_PUBLISH');

    return (
        <div>
            <Navbar user={user} onLogout={handleLogout} />
            <Routes>
                <Route path="/" element={<HomePage user={user} />} />
                <Route path="/articles" element={<ArticlesPage user={user} />} />
                <Route path="/profile" element={<ProfilePage user={user} />} />
                {isPublisher && <Route path="/publisher" element={<PublisherPage />} />}
                {isModerator && <Route path="/write" element={<WriteArticlePage user={user} />} />}
                {isModerator && <Route path="/my-articles" element={<MyArticlesPage user={user} />} />}
                <Route path="*" element={<Navigate to="/" />} />
            </Routes>
        </div>
    );
}

function App() {
    return (
        <BrowserRouter>
            <AppContent />
        </BrowserRouter>
    );
}

const styles = {
    navbar: {
        backgroundColor: '#e63946',
        padding: '15px 30px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
    },
    logo: { color: 'white', margin: 0, cursor: 'pointer' },
    navLinks: { display: 'flex', gap: '10px' },
    navLink: {
        padding: '8px 16px',
        backgroundColor: 'transparent',
        color: 'white',
        border: '1px solid white',
        borderRadius: '6px',
        cursor: 'pointer',
        fontSize: '14px'
    },
    activeLink: {
        padding: '8px 16px',
        backgroundColor: 'white',
        color: '#e63946',
        border: '1px solid white',
        borderRadius: '6px',
        cursor: 'pointer',
        fontSize: '14px',
        fontWeight: 'bold'
    },
    userInfo: { display: 'flex', alignItems: 'center', gap: '15px' },
    userName: { color: 'white' },
    logoutBtn: {
        padding: '8px 16px',
        backgroundColor: 'white',
        color: '#e63946',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer',
        fontWeight: 'bold'
    }
};

export default App;cd ~/Desktop/kun-uz/Kun.uz
git add .
    git commit -m "Add React Router for proper URL navigation"
git push origin main