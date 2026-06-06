import { useState, useEffect, useCallback } from 'react';
import CharityForm from './components/CharityForm';
import CharityFilter from './components/CharityFilter';
import CharityTable from './components/CharityTable';
import './App.css';
import Login from './components/Login';
import charityCaseService from './services/charityCaseService';


import { Client } from '@stomp/stompjs';


function App() {
    const [cases, setCases] = useState([]);
    const [editingCase, setEditingCase] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('token'));
    const [showLogin, setShowLogin] = useState(false);


    const fetchCases = useCallback(async (maxAmount = null) => {
        setLoading(true);
        setError('');
        try {
            const data = await charityCaseService.getAll(maxAmount);
            setCases(data);
        } catch (err) {
            setError('Eroare la incarcarea cazurilor: ' + err.message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        if (!isAuthenticated) return;

        const client = new Client({
            brokerURL: 'ws://localhost:8080/teledon-ws',
            connectHeaders: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('Conectat la WebSocket (Modern)!');

                client.subscribe('/user/queue/cases-updates', () => {
                    console.log('Notificare privata primita de la server! Reincarcam tabelul...');
                    fetchCases();
                });
            },
            onStompError: (frame) => {
                console.error('Eroare in brokerul STOMP: ' + frame.headers['message']);
            },
        });

        client.activate();

        return () => {
            if (client) {
                client.deactivate();
            }
        };
    }, [fetchCases, isAuthenticated]);


    useEffect(() => {
        fetchCases();
    }, [fetchCases]);


    const handleAdd = async (charityCase) => {
        try {
            await charityCaseService.create(charityCase);
        } catch (err) {
            setError('Eroare la adăugare: ' + err.message);
        }
    };

    const handleUpdate = async (charityCase) => {
        try {
            await charityCaseService.update(charityCase);
            setEditingCase(null);
        } catch (err) {
            setError('Eroare la modificare: ' + err.message);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Sigur vrei sa ștergi acest caz caritabil?')) {
            try {
                await charityCaseService.delete(id);
            } catch (err) {
                setError('Eroare la ștergere: ' + err.message);
            }
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('token'); // Ștergem ecusonul (token-ul JWT)
        setIsAuthenticated(false);
        setShowLogin(false);
    };

    const handleEdit = (charityCase) => setEditingCase(charityCase);
    const handleCancelEdit = () => setEditingCase(null);
    const handleFilter = (maxAmount) => fetchCases(maxAmount);

    return (
        <div className="app">
            <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h1>Gestionare Cazuri Caritabile</h1>
                <div>
                    {!isAuthenticated ? (
                        <button onClick={() => setShowLogin(!showLogin)}>
                            {showLogin ? 'Inapoi' : 'Login Voluntar'}
                        </button>
                    ) : (
                        <button onClick={handleLogout}>Logout Voluntar</button>
                    )}
                </div>
            </header>

            {error && <div className="error-banner" style={{ color: 'red', margin: '10px 0' }}>{error}</div>}

            <main>
                {/* Formularul de Login */}
                {showLogin && !isAuthenticated && (
                    <Login onLoginSuccess={() => {
                        setIsAuthenticated(true);
                        setShowLogin(false);
                        fetchCases();
                    }} />
                )}

                {/* Zona securizata: doar voluntarii pot vedea asta */}
                {isAuthenticated && (
                    <section className="form-section">
                        <h2>{editingCase ? 'Editeaza Cazul Caritabil' : 'Adauga Caz Nou'}</h2>
                        <CharityForm
                            onSubmit={editingCase ? handleUpdate : handleAdd}
                            editingCase={editingCase}
                            onCancel={handleCancelEdit}
                        />
                    </section>
                )}

                <section className="filter-section">
                    <h3>Filtrare dupa suma</h3>
                    <CharityFilter onFilter={handleFilter} />
                </section>

                <section className="table-section">
                    {loading ? <p>Se incarca datele din timp real...</p> : (
                        <CharityTable
                            cases={cases}
                            onEdit={handleEdit}
                            onDelete={handleDelete}
                            isVoluntar={isAuthenticated}
                        />
                    )}
                </section>
            </main>
        </div>
    );
}

export default App;
