import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1';

function MyArticlesPage() {
    const [articles, setArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        loadMyArticles();
    }, []);

    const loadMyArticles = async () => {
        try {
            const response = await axios.post(`${API_URL}/article/moderator/filter`, {});
            setArticles(response.data.content);
        } catch (err) {
            setError('Failed to load articles!');
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div style={styles.loading}>Loading...</div>;
    if (error) return <div style={styles.error}>{error}</div>;

    return (
        <div style={styles.content}>
            <h3 style={styles.title}>📝 My Articles</h3>

            {articles.length === 0 ? (
                <p style={styles.empty}>You haven't written any articles yet!</p>
            ) : (
                articles.map((article) => (
                    <div key={article.id} style={styles.card}>
                        <div style={styles.cardHeader}>
                            <h4 style={styles.articleTitle}>{article.title}</h4>
                            <span style={article.status === 'PUBLISHED'
                                ? styles.published : styles.notPublished}>
                                {article.status === 'PUBLISHED' ? '🟢 Published' : '🔴 Not Published'}
                            </span>
                        </div>
                        <p style={styles.description}>{article.description}</p>
                        <div style={styles.footer}>
                            <span style={styles.meta}>⏱️ {article.readTime} min read</span>
                            <span style={styles.meta}>👁️ {article.viewCount || 0} views</span>
                            <span style={styles.meta}>❤️ {article.likeCount || 0} likes</span>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
}

const styles = {
    content: {
        maxWidth: '800px',
        margin: '30px auto',
        padding: '0 20px'
    },
    title: {
        color: '#333',
        marginBottom: '20px'
    },
    card: {
        backgroundColor: 'white',
        padding: '20px',
        borderRadius: '10px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        marginBottom: '15px'
    },
    cardHeader: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '10px'
    },
    articleTitle: {
        color: '#333',
        margin: 0,
        fontSize: '16px'
    },
    published: {
        backgroundColor: '#d4edda',
        color: '#155724',
        padding: '4px 12px',
        borderRadius: '20px',
        fontSize: '12px',
        fontWeight: 'bold'
    },
    notPublished: {
        backgroundColor: '#f8d7da',
        color: '#721c24',
        padding: '4px 12px',
        borderRadius: '20px',
        fontSize: '12px',
        fontWeight: 'bold'
    },
    description: {
        color: '#666',
        fontSize: '14px',
        margin: '0 0 10px'
    },
    footer: {
        display: 'flex',
        gap: '20px'
    },
    meta: {
        color: '#888',
        fontSize: '13px'
    },
    loading: {
        textAlign: 'center',
        marginTop: '100px',
        fontSize: '18px'
    },
    error: {
        textAlign: 'center',
        marginTop: '100px',
        color: 'red'
    },
    empty: {
        textAlign: 'center',
        color: '#888',
        marginTop: '50px',
        fontSize: '18px'
    }
};

export default MyArticlesPage;