import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { getSavedArticles, removeSavedArticle, saveArticle } from '../services/api';

const API_URL = 'http://localhost:8080/api/v1';

function HomePage({ user }) {
    const [articles, setArticles] = useState([]);
    const [savedArticles, setSavedArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState('');
    const [activeTab, setActiveTab] = useState('latest');

    useEffect(() => {
        loadArticles();
        loadSavedArticles();
    }, []);

    const loadArticles = async () => {
        try {
            const response = await axios.get(`${API_URL}/article/by-region/1/10`);
            setArticles(response.data);
        } catch (err) {
            console.error('Failed to load articles');
        } finally {
            setLoading(false);
        }
    };

    const loadSavedArticles = async () => {
        try {
            const response = await getSavedArticles();
            setSavedArticles(response.data);
        } catch (err) {
            console.error('Failed to load saved articles');
        }
    };

    const handleSave = async (articleId) => {
        try {
            await saveArticle(articleId);
            setMessage('Article saved! ✅');
            loadSavedArticles();
            setTimeout(() => setMessage(''), 3000);
        } catch (err) {
            setMessage('Already saved or failed! ❌');
            setTimeout(() => setMessage(''), 3000);
        }
    };

    const handleRemove = async (articleId) => {
        try {
            await removeSavedArticle(articleId);
            setMessage('Article removed!');
            loadSavedArticles();
            setTimeout(() => setMessage(''), 3000);
        } catch (err) {
            setMessage('Failed to remove!');
            setTimeout(() => setMessage(''), 3000);
        }
    };

    return (
        <div style={styles.container}>
            {/* Welcome Banner */}
            <div style={styles.banner}>
                <h2 style={styles.bannerText}>
                    Welcome back, {user.name}! 👋
                </h2>
                <p style={styles.bannerSub}>Stay up to date with the latest news</p>
            </div>

            {/* Tabs */}
            <div style={styles.tabs}>
                <button
                    style={activeTab === 'latest' ? styles.activeTab : styles.tab}
                    onClick={() => setActiveTab('latest')}>
                    📰 Latest News
                </button>
                <button
                    style={activeTab === 'saved' ? styles.activeTab : styles.tab}
                    onClick={() => setActiveTab('saved')}>
                    🔖 Saved ({savedArticles.length})
                </button>
            </div>

            {message && <p style={styles.message}>{message}</p>}

            <div style={styles.content}>
                {/* Latest Articles Tab */}
                {activeTab === 'latest' && (
                    <>
                        {loading ? (
                            <p style={styles.empty}>Loading...</p>
                        ) : articles.length === 0 ? (
                            <p style={styles.empty}>No articles yet!</p>
                        ) : (
                            <div style={styles.grid}>
                                {articles.map((article) => (
                                    <div key={article.id} style={styles.card}>
                                        <h4 style={styles.articleTitle}>{article.title}</h4>
                                        <p style={styles.description}>{article.description}</p>
                                        <div style={styles.footer}>
                                            <span style={styles.meta}>⏱️ {article.readTime} min</span>
                                            <span style={styles.meta}>👁️ {article.viewCount || 0}</span>
                                            <span style={styles.meta}>❤️ {article.likeCount || 0}</span>
                                        </div>
                                        <button
                                            style={styles.saveBtn}
                                            onClick={() => handleSave(article.id)}>
                                            🔖 Save
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </>
                )}

                {/* Saved Articles Tab */}
                {activeTab === 'saved' && (
                    <>
                        {savedArticles.length === 0 ? (
                            <p style={styles.empty}>No saved articles yet!</p>
                        ) : (
                            savedArticles.map((item) => (
                                <div key={item.id} style={styles.savedCard}>
                                    <div>
                                        <p style={styles.savedId}>Article ID: {item.articleId}</p>
                                        <p style={styles.savedDate}>
                                            Saved: {new Date(item.createdDate).toLocaleDateString()}
                                        </p>
                                    </div>
                                    <button
                                        style={styles.removeBtn}
                                        onClick={() => handleRemove(item.articleId)}>
                                        🗑️ Remove
                                    </button>
                                </div>
                            ))
                        )}
                    </>
                )}
            </div>
        </div>
    );
}

const styles = {
    container: {
        minHeight: '100vh',
        backgroundColor: '#f0f2f5'
    },
    banner: {
        backgroundColor: '#e63946',
        padding: '30px',
        textAlign: 'center'
    },
    bannerText: {
        color: 'white',
        margin: 0,
        fontSize: '24px'
    },
    bannerSub: {
        color: 'rgba(255,255,255,0.85)',
        margin: '8px 0 0'
    },
    tabs: {
        display: 'flex',
        gap: '10px',
        maxWidth: '1000px',
        margin: '20px auto 0',
        padding: '0 20px'
    },
    tab: {
        padding: '10px 20px',
        backgroundColor: 'white',
        border: '1px solid #ddd',
        borderRadius: '6px',
        cursor: 'pointer',
        fontSize: '14px',
        color: '#555'
    },
    activeTab: {
        padding: '10px 20px',
        backgroundColor: '#e63946',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer',
        fontSize: '14px',
        color: 'white',
        fontWeight: 'bold'
    },
    content: {
        maxWidth: '1000px',
        margin: '20px auto',
        padding: '0 20px'
    },
    grid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
        gap: '20px'
    },
    card: {
        backgroundColor: 'white',
        padding: '20px',
        borderRadius: '10px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        display: 'flex',
        flexDirection: 'column',
        gap: '10px'
    },
    articleTitle: {
        color: '#333',
        margin: 0,
        fontSize: '16px'
    },
    description: {
        color: '#666',
        fontSize: '14px',
        margin: 0,
        flex: 1
    },
    footer: {
        display: 'flex',
        gap: '12px'
    },
    meta: {
        color: '#888',
        fontSize: '13px'
    },
    saveBtn: {
        padding: '8px',
        backgroundColor: '#e63946',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer',
        fontSize: '14px'
    },
    savedCard: {
        backgroundColor: 'white',
        padding: '20px',
        borderRadius: '10px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        marginBottom: '15px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
    },
    savedId: { margin: 0, fontWeight: 'bold', color: '#333' },
    savedDate: { margin: '5px 0 0', color: '#888', fontSize: '13px' },
    removeBtn: {
        padding: '8px 16px',
        backgroundColor: '#e63946',
        color: 'white',
        border: 'none',
        borderRadius: '6px',
        cursor: 'pointer'
    },
    message: {
        textAlign: 'center',
        fontWeight: 'bold',
        color: 'green',
        margin: '10px 0'
    },
    empty: {
        color: '#888',
        textAlign: 'center',
        marginTop: '50px',
        fontSize: '18px'
    }
};

export default HomePage;