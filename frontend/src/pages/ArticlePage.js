import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1';

function ArticlesPage({ user }) {
    const [articles, setArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');

    useEffect(() => {
        loadArticles();
    }, []);

    const loadArticles = async () => {
    try {
        const response = await axios.get(`${API_URL}/article/by-region/1/10`);
        setArticles(response.data);
    } catch (err) {
        setError('Failed to load articles!');
    } finally {
        setLoading(false);
    }
};

    const handleSave = async (articleId) => {
        try {
            await axios.post(`${API_URL}/saved-article`, { articleId });
            setMessage('Article saved! ✅');
            setTimeout(() => setMessage(''), 3000);
        } catch (err) {
            setMessage('Failed to save article! ❌');
            setTimeout(() => setMessage(''), 3000);
        }
    };

    if (loading) return <div style={styles.loading}>Loading articles...</div>;
    if (error) return <div style={styles.error}>{error}</div>;

    return (
        <div style={styles.content}>
            <h3 style={styles.title}>📰 Latest Articles</h3>
            {message && <p style={styles.message}>{message}</p>}

            {articles.length === 0 ? (
                <p style={styles.empty}>No published articles yet!</p>
            ) : (
                <div style={styles.grid}>
                    {articles.map((article) => (
                        <div key={article.id} style={styles.card}>
                            <h4 style={styles.articleTitle}>{article.title}</h4>
                            <p style={styles.description}>{article.description}</p>
                            <div style={styles.footer}>
                                <span style={styles.readTime}>
                                    ⏱️ {article.readTime} min read
                                </span>
                                <div style={styles.stats}>
                                    <span>👁️ {article.viewCount || 0}</span>
                                    <span>❤️ {article.likeCount || 0}</span>
                                </div>
                            </div>
                            <button
                                style={styles.saveBtn}
                                onClick={() => handleSave(article.id)}>
                                🔖 Save Article
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

const styles = {
    content: {
        maxWidth: '1000px',
        margin: '30px auto',
        padding: '0 20px'
    },
    title: {
        color: '#333',
        marginBottom: '20px'
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
        justifyContent: 'space-between',
        alignItems: 'center',
        color: '#888',
        fontSize: '13px'
    },
    stats: {
        display: 'flex',
        gap: '10px'
    },
    readTime: {
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
    message: {
        color: 'green',
        fontWeight: 'bold',
        textAlign: 'center'
    },
    loading: {
        textAlign: 'center',
        marginTop: '100px',
        fontSize: '18px'
    },
    error: {
        textAlign: 'center',
        marginTop: '100px',
        color: 'red',
        fontSize: '18px'
    },
    empty: {
        color: '#888',
        textAlign: 'center',
        marginTop: '50px',
        fontSize: '18px'
    }
};

export default ArticlesPage;