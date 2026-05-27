import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
import ProfilePage from './pages/ProfilePage';
import ArticlesPage from './pages/ArticlePage';
import WriteArticlePage from './pages/WriteArticlePage';
import MyArticlesPage from './pages/MyArticlesPage';
import PublisherPage from './pages/PublisherPage';

const API_URL = 'http://localhost:8080/api/v1';

function Layout({ user, onLogout, children }) {
    const navigate = useNavigate();
    const location = useLocation();
    const [categories, setCategories] = useState([]);
    const [sections, setSections] = useState([]);
    const [regions, setRegions] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [selectedSection, setSelectedSection] = useState(null);
    const [selectedRegion, setSelectedRegion] = useState('');

    const isModerator = user.roleList && user.roleList.includes('ROLE_MODERATOR');
    const isPublisher = user.roleList && user.roleList.includes('ROLE_PUBLISH');

    useEffect(() => {
        loadFilters();
    }, []);

    const loadFilters = async () => {
        try {
            const config = {
                headers: {
                    'Accept-Language': 'EN',
                    Authorization: `Bearer ${localStorage.getItem('jwt')}`
                }
            };
            const [catRes, secRes, regRes] = await Promise.all([
                axios.get(`${API_URL}/category/lang`, config),
                axios.get(`${API_URL}/section/lang`, config),
                axios.get(`${API_URL}/region/lang`, config)
            ]);
            setCategories(catRes.data);
            setSections(secRes.data);
            setRegions(regRes.data);
        } catch (err) {
            console.error('Failed to load filters', err);
        }
    };

    const isActive = (path) => location.pathname === path;

    const handleCategoryClick = (catId) => {
        setSelectedCategory(selectedCategory === catId ? null : catId);
        navigate('/articles', { state: { categoryId: selectedCategory === catId ? null : catId, sectionId: selectedSection, regionId: selectedRegion || null } });
    };

    const handleSectionClick = (secId) => {
        setSelectedSection(selectedSection === secId ? null : secId);
        navigate('/articles', { state: { categoryId: selectedCategory, sectionId: selectedSection === secId ? null : secId, regionId: selectedRegion || null } });
    };

    const handleRegionChange = (e) => {
        setSelectedRegion(e.target.value);
        navigate('/articles', { state: { categoryId: selectedCategory, sectionId: selectedSection, regionId: e.target.value || null } });
    };

    return (
        <div style={styles.wrapper}>
            {/* Top Navbar */}
            <div style={styles.navbar}>
                <div style={styles.logo} onClick={() => navigate('/')}>KUN<span style={styles.logoUz}>.UZ</span></div>
                <div style={styles.navCats}>
                    {categories.map(cat => (
                        <button
                            key={cat.id}
                            style={selectedCategory === cat.id ? styles.navCatActive : styles.navCat}
                            onClick={() => handleCategoryClick(cat.id)}>
                            {cat.name}
                        </button>
                    ))}
                </div>
                <div style={styles.navRight}>
                    <select style={styles.regionSelect} value={selectedRegion} onChange={handleRegionChange}>
                        <option value="">All Regions</option>
                        {regions.map(reg => (
                            <option key={reg.id} value={reg.id}>{reg.name}</option>
                        ))}
                    </select>
                    <span style={styles.userName}>{user.name} {user.surname}</span>
                    <button style={styles.logoutBtn} onClick={onLogout}>Logout</button>
                </div>
            </div>

            {/* Sub Navbar - Sections */}
            <div style={styles.subnav}>
                <button
                    style={selectedSection === null ? styles.subnavBtnActive : styles.subnavBtn}
                    onClick={() => { setSelectedSection(null); navigate('/articles'); }}>
                    All
                </button>
                {sections.map(sec => (
                    <button
                        key={sec.id}
                        style={selectedSection === sec.id ? styles.subnavBtnActive : styles.subnavBtn}
                        onClick={() => handleSectionClick(sec.id)}>
                        {sec.name}
                    </button>
                ))}
            </div>

            {/* Sidebar + Content */}
            <div style={styles.sidebarLayout}>
                <div style={styles.sidebar}>
                    <div
                        style={isActive('/') ? styles.sidebarLinkActive : styles.sidebarLink}
                        onClick={() => navigate('/')}>
                        <span>🏠</span> Home
                    </div>
                    <div
                        style={isActive('/articles') ? styles.sidebarLinkActive : styles.sidebarLink}
                        onClick={() => navigate('/articles')}>
                        <span>📰</span> Articles
                    </div>
                    {isPublisher && (
                        <div
                            style={isActive('/publisher') ? styles.sidebarLinkActive : styles.sidebarLink}
                            onClick={() => navigate('/publisher')}>
                            <span>📋</span> Publisher
                        </div>
                    )}
                    {isModerator && (
                        <div
                            style={isActive('/write') ? styles.sidebarLinkActive : styles.sidebarLink}
                            onClick={() => navigate('/write')}>
                            <span>✍️</span> Write
                        </div>
                    )}
                    {isModerator && (
                        <div
                            style={isActive('/my-articles') ? styles.sidebarLinkActive : styles.sidebarLink}
                            onClick={() => navigate('/my-articles')}>
                            <span>📝</span> My Articles
                        </div>
                    )}
                    <div
                        style={isActive('/profile') ? styles.sidebarLinkActive : styles.sidebarLink}
                        onClick={() => navigate('/profile')}>
                        <span>👤</span> Profile
                    </div>
                </div>

                <div style={styles.mainContent}>
                    {children}
                </div>
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
        <Layout user={user} onLogout={handleLogout}>
            <Routes>
                <Route path="/" element={<HomePage user={user} />} />
                <Route path="/articles" element={<ArticlesPage user={user} />} />
                <Route path="/profile" element={<ProfilePage user={user} />} />
                {isPublisher && <Route path="/publisher" element={<PublisherPage />} />}
                {isModerator && <Route path="/write" element={<WriteArticlePage user={user} />} />}
                {isModerator && <Route path="/my-articles" element={<MyArticlesPage user={user} />} />}
                <Route path="*" element={<Navigate to="/" />} />
            </Routes>
        </Layout>
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
    wrapper: { minHeight: '100vh', backgroundColor: '#f0f2f5' },
    navbar: {
        backgroundColor: '#e63946',
        padding: '0 20px',
        display: 'flex',
        alignItems: 'center',
        gap: '16px',
        height: '52px',
        position: 'sticky',
        top: 0,
        zIndex: 100
    },
    logo: { color: '#fff', fontSize: '18px', fontWeight: '700', cursor: 'pointer', whiteSpace: 'nowrap' },
    logoUz: { fontWeight: '400', opacity: 0.85 },
    navCats: { display: 'flex', gap: '2px', flex: 1, overflowX: 'auto', alignItems: 'center', height: '100%' },
    navCat: { padding: '6px 14px', borderRadius: '20px', border: 'none', background: 'none', color: 'rgba(255,255,255,0.8)', fontSize: '13px', cursor: 'pointer', whiteSpace: 'nowrap' },
    navCatActive: { padding: '6px 14px', borderRadius: '20px', border: 'none', background: 'rgba(255,255,255,0.25)', color: '#fff', fontSize: '13px', cursor: 'pointer', whiteSpace: 'nowrap', fontWeight: '500' },
    navRight: { display: 'flex', alignItems: 'center', gap: '10px', flexShrink: 0 },
    regionSelect: { padding: '5px 10px', borderRadius: '20px', border: '1px solid rgba(255,255,255,0.4)', background: 'rgba(255,255,255,0.15)', color: '#fff', fontSize: '12px', cursor: 'pointer', outline: 'none' },
    userName: { color: 'rgba(255,255,255,0.9)', fontSize: '13px', whiteSpace: 'nowrap' },
    logoutBtn: { padding: '5px 14px', borderRadius: '20px', border: '1px solid rgba(255,255,255,0.6)', background: 'rgba(255,255,255,0.15)', color: '#fff', fontSize: '12px', cursor: 'pointer' },
    subnav: { backgroundColor: '#fff', borderBottom: '0.5px solid #eee', padding: '0 20px', display: 'flex', alignItems: 'center', gap: '4px', height: '40px', overflowX: 'auto', position: 'sticky', top: '52px', zIndex: 99 },
    subnavBtn: { padding: '4px 14px', borderRadius: '20px', border: 'none', background: 'none', color: '#666', fontSize: '12px', cursor: 'pointer', whiteSpace: 'nowrap' },
    subnavBtnActive: { padding: '4px 14px', borderRadius: '20px', border: 'none', background: '#fff0f1', color: '#e63946', fontSize: '12px', cursor: 'pointer', whiteSpace: 'nowrap', fontWeight: '500' },
    sidebarLayout: { display: 'flex', minHeight: 'calc(100vh - 92px)' },
    sidebar: { width: '180px', borderRight: '0.5px solid #eee', backgroundColor: '#fff', padding: '12px 0', flexShrink: 0, position: 'sticky', top: '92px', height: 'calc(100vh - 92px)', overflowY: 'auto' },
    sidebarLink: { display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 18px', fontSize: '13px', color: '#666', cursor: 'pointer', borderLeft: '3px solid transparent' },
    sidebarLinkActive: { display: 'flex', alignItems: 'center', gap: '10px', padding: '10px 18px', fontSize: '13px', color: '#e63946', cursor: 'pointer', borderLeft: '3px solid #e63946', backgroundColor: '#fff0f1', fontWeight: '500' },
    mainContent: { flex: 1, padding: '20px', backgroundColor: '#f0f2f5' }
};

export default App;