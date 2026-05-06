import React, { useState } from 'react';
import LoginPage from './pages/LoginPage';
import HomePage from './pages/HomePage';
import ProfilePage from './pages/ProfilePage';
import ArticlesPage from './pages/ArticlePage';
import WriteArticlePage from './pages/WriteArticlePage';
import MyArticlesPage from './pages/MyArticlesPage';

function App() {
    const [user, setUser] = useState(
        JSON.parse(localStorage.getItem('user')) || null
    );
    const [currentPage, setCurrentPage] = useState('home');

    const handleLogin = (userData) => {
        setUser(userData);
        setCurrentPage('home');
    };

    const handleLogout = () => {
        localStorage.removeItem('jwt');
        localStorage.removeItem('user');
        setUser(null);
        setCurrentPage('home');
    };

    if (!user) {
        return <LoginPage onLogin={handleLogin} />;
    }

    const isModerator = user.roleList && user.roleList.includes('ROLE_MODERATOR');

    return (
        <div>
            <div style={styles.navbar}>
                <h2 style={styles.logo}>🗞️ Kun.uz</h2>
                <div style={styles.navLinks}>
                    <button
                        style={currentPage === 'home' ? styles.activeLink : styles.navLink}
                        onClick={() => setCurrentPage('home')}>
                        🏠 Home
                    </button>
                    <button
                        style={currentPage === 'articles' ? styles.activeLink : styles.navLink}
                        onClick={() => setCurrentPage('articles')}>
                        📰 Articles
                    </button>
                    {isModerator && (
                        <button
                            style={currentPage === 'write' ? styles.activeLink : styles.navLink}
                            onClick={() => setCurrentPage('write')}>
                            ✍️ Write
                        </button>
                    )}
                    {isModerator && (
                        <button
                            style={currentPage === 'myarticles' ? styles.activeLink : styles.navLink}
                            onClick={() => setCurrentPage('myarticles')}>
                            📝 My Articles
                        </button>
                    )}
                    <button
                        style={currentPage === 'profile' ? styles.activeLink : styles.navLink}
                        onClick={() => setCurrentPage('profile')}>
                        👤 Profile
                    </button>
                </div>
                <div style={styles.userInfo}>
                    <span style={styles.userName}>
                        {user.name} {user.surname}
                    </span>
                    <button style={styles.logoutBtn} onClick={handleLogout}>
                        Logout
                    </button>
                </div>
            </div>

            {currentPage === 'home' && <HomePage user={user} />}
            {currentPage === 'articles' && <ArticlesPage user={user} />}
            {currentPage === 'write' && <WriteArticlePage user={user} />}
            {currentPage === 'myarticles' && <MyArticlesPage user={user} />}
            {currentPage === 'profile' && <ProfilePage user={user} />}
        </div>
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
    logo: { color: 'white', margin: 0 },
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
    userInfo: {
        display: 'flex',
        alignItems: 'center',
        gap: '15px'
    },
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

export default App;