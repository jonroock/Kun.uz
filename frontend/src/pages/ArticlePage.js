import React, { useState, useEffect } from 'react';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1';

function ArticlesPage({ user }) {
    const [articles, setArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');
    const [likeCounts, setLikeCounts] = useState({});
    const [likedArticles, setLikedArticles] = useState({});
    const [openComments, setOpenComments] = useState({});
    const [comments, setComments] = useState({});
    const [newComment, setNewComment] = useState({});

    const getHeaders = () => ({
        headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` }
    });

    useEffect(() => {
        loadArticles();
    }, []);

    const loadArticles = async () => {
        try {
            const response = await axios.get(`${API_URL}/article/by-region/1/10`);
            setArticles(response.data);
            const counts = {};
            const liked = {};
            await Promise.all(response.data.map(async (article) => {
                try {
                    const res = await axios.get(`${API_URL}/article-like/count/${article.id}`);
                    counts[article.id] = res.data;
                } catch { counts[article.id] = 0; }
                try {
                    const res = await axios.get(`${API_URL}/article-like/my/${article.id}`, getHeaders());
                    liked[article.id] = res.data;
                } catch { liked[article.id] = false; }
            }));
            setLikeCounts(counts);
            setLikedArticles(liked);
        } catch (err) {
            setError('Failed to load articles!');
        } finally {
            setLoading(false);
        }
    };

    const handleLike = async (articleId) => {
        try {
            if (likedArticles[articleId]) {
                await axios.put(`${API_URL}/article-like/remove/${articleId}`, {}, getHeaders());
                setLikeCounts(prev => ({ ...prev, [articleId]: Math.max((prev[articleId] || 1) - 1, 0) }));
                setLikedArticles(prev => ({ ...prev, [articleId]: false }));
                showMessage('Like removed! 🤍');
            } else {
                await axios.post(`${API_URL}/article-like`, { articleId, emotion: 'LIKE' }, getHeaders());
                setLikeCounts(prev => ({ ...prev, [articleId]: (prev[articleId] || 0) + 1 }));
                setLikedArticles(prev => ({ ...prev, [articleId]: true }));
                showMessage('Liked! ❤️');
            }
        } catch (err) {
            showMessage('Failed! ❌');
        }
    };

    const handleSave = async (articleId) => {
        try {
            await axios.post(`${API_URL}/saved-article`, { articleId }, getHeaders());
            showMessage('Article saved! ✅');
        } catch (err) {
            showMessage('Failed to save! ❌');
        }
    };

    const toggleComments = async (articleId) => {
        const isOpen = openComments[articleId];
        setOpenComments(prev => ({ ...prev, [articleId]: !isOpen }));
        if (!isOpen && !comments[articleId]) {
            try {
                const response = await axios.get(`${API_URL}/comment/article/${articleId}`);
                setComments(prev => ({ ...prev, [articleId]: response.data.content || [] }));
            } catch { }
        }
    };

    const handleAddComment = async (articleId) => {
        const content = newComment[articleId];
        if (!content || !content.trim()) return;
        try {
            await axios.post(`${API_URL}/comment`, { content, articleId }, getHeaders());
            setNewComment(prev => ({ ...prev, [articleId]: '' }));
            const res = await axios.get(`${API_URL}/comment/article/${articleId}`);
            setComments(prev => ({ ...prev, [articleId]: res.data.content || [] }));
            showMessage('Comment added! 💬');
        } catch { showMessage('Failed to comment! ❌'); }
    };

    const showMessage = (msg) => {
        setMessage(msg);
        setTimeout(() => setMessage(''), 3000);
    };

    if (loading) return <div style={styles.center}>Loading articles...</div>;
    if (error) return <div style={{ ...styles.center, color: 'red' }}>{error}</div>;

    return (
        <div style={styles.container}>
            <h3 style={styles.pageTitle}>📰 Latest Articles</h3>
            {message && <p style={styles.message}>{message}</p>}

            {articles.length === 0 ? (
                <p style={styles.empty}>No published articles yet!</p>
            ) : (
                articles.map((article) => (
                    <div key={article.id} style={styles.card}>
                        <h4 style={styles.articleTitle}>{article.title}</h4>
                        <p style={styles.description}>{article.description}</p>
                        <div style={styles.stats}>
                            <span>⏱️ {article.readTime || 0} min</span>
                            <span>👁️ {article.viewCount || 0} views</span>
                            <span style={{ color: '#e63946' }}>❤️ {likeCounts[article.id] || 0} likes</span>
                        </div>
                        <div style={styles.actions}>
                            <button
                                style={likedArticles[article.id] ? styles.likedBtn : styles.likeBtn}
                                onClick={() => handleLike(article.id)}>
                                {likedArticles[article.id] ? '❤️' : '🤍'} Like
                            </button>
                            <button style={styles.commentBtn} onClick={() => toggleComments(article.id)}>
                                💬 Comments {openComments[article.id] ? '▲' : '▼'}
                            </button>
                            <button style={styles.saveBtn} onClick={() => handleSave(article.id)}>
                                🔖 Save
                            </button>
                        </div>

                        {openComments[article.id] && (
                            <div style={styles.commentsSection}>
                                <div style={styles.addComment}>
                                    <input
                                        style={styles.commentInput}
                                        type="text"
                                        placeholder="Write a comment..."
                                        value={newComment[article.id] || ''}
                                        onChange={(e) => setNewComment(prev => ({ ...prev, [article.id]: e.target.value }))}
                                    />
                                    <button style={styles.postBtn} onClick={() => handleAddComment(article.id)}>Post</button>
                                </div>
                                {(comments[article.id] || []).length === 0 ? (
                                    <p style={styles.noComments}>No comments yet!</p>
                                ) : (
                                    (comments[article.id] || []).map((comment) => (
                                        <div key={comment.id} style={styles.commentCard}>
                                            <p style={styles.commentContent}>{comment.content}</p>
                                            <span style={styles.commentMeta}>
                                                {new Date(comment.createdDate).toLocaleDateString()}
                                            </span>
                                        </div>
                                    ))
                                )}
                            </div>
                        )}
                    </div>
                ))
            )}
        </div>
    );
}

const styles = {
    container: { maxWidth: '800px', margin: '30px auto', padding: '0 20px' },
    pageTitle: { color: '#333', fontSize: '22px', margin: '0 0 20px' },
    card: { backgroundColor: 'white', padding: '24px', borderRadius: '10px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)', marginBottom: '20px' },
    articleTitle: { color: '#333', margin: '0 0 8px', fontSize: '18px' },
    description: { color: '#666', fontSize: '14px', margin: '0 0 12px' },
    stats: { display: 'flex', gap: '16px', color: '#888', fontSize: '13px', marginBottom: '12px' },
    actions: { display: 'flex', gap: '10px', flexWrap: 'wrap' },
    likeBtn: { padding: '8px 14px', backgroundColor: 'white', color: '#333', border: '1px solid #ddd', borderRadius: '6px', cursor: 'pointer' },
    likedBtn: { padding: '8px 14px', backgroundColor: '#e63946', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    commentBtn: { padding: '8px 14px', backgroundColor: '#457b9d', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    saveBtn: { padding: '8px 14px', backgroundColor: '#2a9d8f', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    commentsSection: { marginTop: '16px', borderTop: '1px solid #eee', paddingTop: '16px' },
    addComment: { display: 'flex', gap: '8px', marginBottom: '12px' },
    commentInput: { flex: 1, padding: '10px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px' },
    postBtn: { padding: '10px 16px', backgroundColor: '#e63946', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    commentCard: { backgroundColor: '#f8f9fa', padding: '12px', borderRadius: '6px', marginBottom: '8px' },
    commentContent: { margin: '0 0 4px', color: '#333', fontSize: '14px' },
    commentMeta: { color: '#888', fontSize: '12px' },
    noComments: { color: '#888', textAlign: 'center', fontSize: '14px' },
    message: { textAlign: 'center', fontWeight: 'bold', color: 'green', marginBottom: '16px' },
    center: { textAlign: 'center', marginTop: '100px', fontSize: '18px' },
    empty: { color: '#888', textAlign: 'center', marginTop: '50px', fontSize: '18px' }
};

export default ArticlesPage;