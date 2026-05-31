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
    const [selectedArticle, setSelectedArticle] = useState(null);
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    const [likeCounts, setLikeCounts] = useState({});
    const [likedArticles, setLikedArticles] = useState({});
    const [savedArticleDetails, setSavedArticleDetails] = useState({});

    const getHeaders = () => ({
        headers: { Authorization: `Bearer ${localStorage.getItem('jwt')}` }
    });

    useEffect(() => {
        loadArticles();
        loadSavedArticles();
    }, []);

    const loadArticles = async () => {
        try {
            const response = await axios.post(`${API_URL}/article/filter`, {}, getHeaders());
            setArticles(response.data.content || []);
            fetchLikeCounts(response.data.content || []);
        } catch (err) {
            console.error('Failed to load articles');
        } finally {
            setLoading(false);
        }
    };

    const fetchLikeCounts = async (articleList) => {
        const counts = {};
        const liked = {};
        await Promise.all(articleList.map(async (article) => {
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
    };

    const loadSavedArticles = async () => {
        try {
            const response = await getSavedArticles();
            setSavedArticles(response.data);
            const details = {};
            await Promise.all(response.data.map(async (item) => {
                try {
                    const res = await axios.get(`${API_URL}/article/detail/${item.articleId}`, {
                        headers: {
                            Authorization: `Bearer ${localStorage.getItem('jwt')}`,
                            'Accept-Language': 'UZ'
                        }
                    });
                    details[item.articleId] = res.data;
                } catch (e) {
                    details[item.articleId] = null;
                }
            }));
            setSavedArticleDetails(details);
            const detailList = Object.values(details).filter(d => d !== null);
            if (detailList.length > 0) fetchLikeCounts(detailList);
        } catch (err) {
            console.error('Failed to load saved articles');
        }
    };

    const handleSave = async (articleId) => {
        try {
            await saveArticle(articleId);
            showMessage('Article saved! ✅');
            loadSavedArticles();
        } catch { showMessage('Already saved! ❌'); }
    };

    const handleRemove = async (articleId) => {
        try {
            await removeSavedArticle(articleId);
            showMessage('Article removed!');
            loadSavedArticles();
        } catch { showMessage('Failed to remove!'); }
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
        } catch { showMessage('Failed! ❌'); }
    };

    const openArticle = async (article) => {
        setSelectedArticle(article);
        setNewComment('');
        try {
            const res = await axios.get(`${API_URL}/comment/article/${article.id}`);
            setComments(res.data.content || []);
        } catch { setComments([]); }
        try {
            await axios.get(`${API_URL}/article/view-count/${article.id}`);
        } catch {}
    };

    const closeArticle = () => {
        setSelectedArticle(null);
        setComments([]);
    };

    const handleAddComment = async () => {
        if (!newComment.trim()) return;
        try {
            await axios.post(`${API_URL}/comment`, {
                content: newComment,
                articleId: selectedArticle.id
            }, getHeaders());
            setNewComment('');
            const res = await axios.get(`${API_URL}/comment/article/${selectedArticle.id}`);
            setComments(res.data.content || []);
            showMessage('Comment added! 💬');
        } catch { showMessage('Failed to add comment! ❌'); }
    };

    const showMessage = (msg) => {
        setMessage(msg);
        setTimeout(() => setMessage(''), 3000);
    };

    return (
        <div style={styles.container}>
            <div style={styles.banner}>
                <h2 style={styles.bannerText}>Welcome back, {user.name}! 👋</h2>
                <p style={styles.bannerSub}>Stay up to date with the latest news</p>
            </div>

            <div style={styles.tabs}>
                <button style={activeTab === 'latest' ? styles.activeTab : styles.tab}
                        onClick={() => setActiveTab('latest')}>📰 Latest News</button>
                <button style={activeTab === 'saved' ? styles.activeTab : styles.tab}
                        onClick={() => setActiveTab('saved')}>🔖 Saved ({savedArticles.length})</button>
            </div>

            {message && <p style={styles.message}>{message}</p>}

            <div style={styles.content}>
                {activeTab === 'latest' && (
                    loading ? <p style={styles.empty}>Loading...</p> :
                        articles.length === 0 ? <p style={styles.empty}>No articles yet!</p> :
                            <div style={styles.grid}>
                                {articles.map((article) => (
                                    <div key={article.id} style={styles.card}>
                                        <div style={styles.cardBody}>
                                            <h4 style={styles.articleTitle} onClick={() => openArticle(article)}>
                                                {article.title}
                                            </h4>
                                            <div style={styles.stats}>
                                                <span>⏱️ {article.readTime || 0} min</span>
                                                <span>👁️ {article.viewCount || 0}</span>
                                                <span>❤️ {likeCounts[article.id] || 0}</span>
                                            </div>
                                            <div style={styles.actions}>
                                                <button style={styles.readBtn} onClick={() => openArticle(article)}>
                                                    📖 Read
                                                </button>
                                                <button
                                                    style={likedArticles[article.id] ? styles.likedBtn : styles.likeBtn}
                                                    onClick={() => handleLike(article.id)}>
                                                    {likedArticles[article.id] ? '❤️' : '🤍'} Like
                                                </button>
                                                <button style={styles.saveBtn} onClick={() => handleSave(article.id)}>
                                                    🔖 Save
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                )}

                {activeTab === 'saved' && (
                    savedArticles.length === 0 ?
                        <p style={styles.empty}>No saved articles yet!</p> :
                        <div style={styles.grid}>
                            {savedArticles.map((item) => {
                                const detail = savedArticleDetails[item.articleId];
                                return (
                                    <div key={item.id} style={styles.card}>
                                        <div style={styles.cardBody}>
                                            <h4 style={styles.articleTitle} onClick={() => detail && openArticle(detail)}>
                                                {detail ? detail.title : 'Loading...'}
                                            </h4>
                                            <div style={styles.stats}>
                                                <span>⏱️ {detail ? detail.readTime || 0 : 0} min</span>
                                                <span>👁️ {detail ? detail.viewCount || 0 : 0}</span>
                                                <span>❤️ {detail ? likeCounts[item.articleId] || 0 : 0}</span>
                                            </div>
                                            <div style={styles.actions}>
                                                <button style={styles.readBtn} onClick={() => detail && openArticle(detail)}>
                                                    📖 Read
                                                </button>
                                                <button
                                                    style={likedArticles[item.articleId] ? styles.likedBtn : styles.likeBtn}
                                                    onClick={() => handleLike(item.articleId)}>
                                                    {likedArticles[item.articleId] ? '❤️' : '🤍'} Like
                                                </button>
                                                <button style={styles.removeBtn} onClick={() => handleRemove(item.articleId)}>
                                                    🗑️ Remove
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                )}
            </div>

            {selectedArticle && (
                <div style={styles.overlay} onClick={closeArticle}>
                    <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
                        {selectedArticle.image?.url && (
                            <img
                                src={selectedArticle.image.url}
                                alt={selectedArticle.title}
                                style={styles.modalImage}
                                onError={(e) => e.target.style.display = 'none'}
                            />
                        )}
                        <div style={styles.modalHeader}>
                            <h3 style={styles.modalTitle}>{selectedArticle.title}</h3>
                            <button style={styles.closeBtn} onClick={closeArticle}>✕</button>
                        </div>
                        <p style={styles.modalDescription}>{selectedArticle.description}</p>
                        <div style={styles.modalStats}>
                            <span>⏱️ {selectedArticle.readTime || 0} min read</span>
                            <span>👁️ {selectedArticle.viewCount || 0} views</span>
                            <span>❤️ {likeCounts[selectedArticle.id] || 0} likes</span>
                        </div>
                        <div style={styles.modalContent}>
                            {selectedArticle.content || 'No content available.'}
                        </div>
                        <div style={styles.commentsSection}>
                            <h4 style={styles.commentsTitle}>💬 Comments</h4>
                            <div style={styles.addComment}>
                                <input
                                    style={styles.commentInput}
                                    type="text"
                                    placeholder="Write a comment..."
                                    value={newComment}
                                    onChange={(e) => setNewComment(e.target.value)}
                                />
                                <button style={styles.postBtn} onClick={handleAddComment}>Post</button>
                            </div>
                            {comments.length === 0 ?
                                <p style={styles.noComments}>No comments yet. Be the first!</p> :
                                comments.map((comment) => (
                                    <div key={comment.id} style={styles.commentCard}>
                                        <p style={styles.commentContent}>{comment.content}</p>
                                        <span style={styles.commentMeta}>
                                            {new Date(comment.createdDate).toLocaleDateString()}
                                        </span>
                                    </div>
                                ))
                            }
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

const styles = {
    container: { minHeight: '100vh', backgroundColor: '#f0f2f5' },
    banner: { backgroundColor: '#e63946', padding: '30px', textAlign: 'center' },
    bannerText: { color: 'white', margin: 0, fontSize: '24px' },
    bannerSub: { color: 'rgba(255,255,255,0.85)', margin: '8px 0 0' },
    tabs: { display: 'flex', gap: '10px', maxWidth: '1000px', margin: '20px auto 0', padding: '0 20px' },
    tab: { padding: '10px 20px', backgroundColor: 'white', border: '1px solid #ddd', borderRadius: '6px', cursor: 'pointer', fontSize: '14px', color: '#555' },
    activeTab: { padding: '10px 20px', backgroundColor: '#e63946', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '14px', color: 'white', fontWeight: 'bold' },
    content: { maxWidth: '1000px', margin: '20px auto', padding: '0 20px' },
    grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '16px', alignItems: 'start' },
    card: { backgroundColor: 'white', borderRadius: '10px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)', overflow: 'hidden' },
    cardBody: { padding: '16px', display: 'flex', flexDirection: 'column', gap: '10px' },
    articleTitle: { color: '#333', margin: 0, fontSize: '15px', fontWeight: '500', cursor: 'pointer', lineHeight: '1.4' },
    stats: { display: 'flex', gap: '12px', color: '#888', fontSize: '13px' },
    actions: { display: 'flex', gap: '8px', flexWrap: 'wrap' },
    readBtn: { padding: '6px 12px', backgroundColor: '#457b9d', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '12px' },
    likeBtn: { padding: '6px 12px', backgroundColor: 'white', color: '#333', border: '1px solid #ddd', borderRadius: '6px', cursor: 'pointer', fontSize: '12px' },
    likedBtn: { padding: '6px 12px', backgroundColor: '#e63946', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '12px' },
    saveBtn: { padding: '6px 12px', backgroundColor: '#2a9d8f', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '12px' },
    removeBtn: { padding: '6px 12px', backgroundColor: '#e63946', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '12px' },
    message: { textAlign: 'center', fontWeight: 'bold', color: 'green', margin: '10px 0' },
    empty: { color: '#888', textAlign: 'center', marginTop: '50px', fontSize: '18px' },
    overlay: { position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'flex-start', zIndex: 1000, overflowY: 'auto', padding: '30px 20px' },
    modal: { backgroundColor: 'white', borderRadius: '12px', maxWidth: '700px', width: '100%', maxHeight: '90vh', overflowY: 'auto', overflow: 'hidden' },
    modalImage: { width: '100%', height: '250px', objectFit: 'cover', display: 'block' },
    modalHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', padding: '20px 24px 0' },
    modalTitle: { color: '#333', margin: 0, fontSize: '20px', flex: 1, lineHeight: '1.4' },
    closeBtn: { background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer', color: '#888', marginLeft: '10px', flexShrink: 0 },
    modalDescription: { color: '#666', fontSize: '14px', margin: '10px 24px', lineHeight: '1.6' },
    modalStats: { display: 'flex', gap: '16px', color: '#888', fontSize: '13px', padding: '0 24px 12px', borderBottom: '1px solid #eee' },
    modalContent: { color: '#333', fontSize: '15px', lineHeight: '1.8', padding: '16px 24px', whiteSpace: 'pre-wrap' },
    commentsSection: { borderTop: '1px solid #eee', padding: '16px 24px' },
    commentsTitle: { margin: '0 0 12px', color: '#333' },
    addComment: { display: 'flex', gap: '8px', marginBottom: '16px' },
    commentInput: { flex: 1, padding: '10px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px' },
    postBtn: { padding: '10px 16px', backgroundColor: '#e63946', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' },
    commentCard: { backgroundColor: '#f8f9fa', padding: '12px', borderRadius: '6px', marginBottom: '8px' },
    commentContent: { margin: '0 0 4px', color: '#333', fontSize: '14px' },
    commentMeta: { color: '#888', fontSize: '12px' },
    noComments: { color: '#888', textAlign: 'center', fontSize: '14px' }
};

export default HomePage;