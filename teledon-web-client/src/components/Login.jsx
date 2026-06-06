import { useState } from 'react';
import authService from '../services/authService';

function Login({ onLoginSuccess }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(false);

        if (!username || !password) {
            setError('Te rugam sa completezi toate campurile!');
            return;
        }

        setLoading(true);
        try {
            const token = await authService.login(username, password);

            if (token && !token.startsWith('Eroare:')) {
                localStorage.setItem('token', token);
                onLoginSuccess();
            } else {
                setError(token || 'Credentiale invalide!');
            }
        } catch (err) {
            setError('Eroare la conectare: ' + (err.response?.data || err.message));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container" style={{ maxWidth: '400px', margin: '20px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '5px', backgroundColor: '#ffffff' }}>
            <h2>Autentificare Voluntar</h2>
            {error && <div className="error-message" style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', marginBottom: '5px', color: '#333' }}>Utilizator:</label>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        style={{ width: '100%', padding: '8px', boxSizing: 'border-box', backgroundColor: '#ffffff', color: '#333333', border: '1px solid #ddd', borderRadius: '4px' }}
                        disabled={loading}
                    />
                </div>
                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', marginBottom: '5px', color: '#333' }}>Parola:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        style={{ width: '100%', padding: '8px', boxSizing: 'border-box', backgroundColor: '#ffffff', color: '#333333', border: '1px solid #ddd', borderRadius: '4px' }}
                        disabled={loading}
                    />
                </div>
                <button type="submit" style={{ width: '100%', padding: '10px', background: '#007bff', color: 'white', border: 'none', borderRadius: '3px', cursor: 'pointer' }} disabled={loading}>
                    {loading ? 'Se verifica...' : 'Intra in cont'}
                </button>
            </form>
        </div>
    );
}

export default Login;
