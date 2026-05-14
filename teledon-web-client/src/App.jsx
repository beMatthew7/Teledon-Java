import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import CharityForm from './components/CharityForm';
import CharityFilter from './components/CharityFilter';
import CharityTable from './components/CharityTable';
import './App.css';

const API_URL = 'http://localhost:8080/teledon/charity-cases';

function App() {
  const [cases, setCases] = useState([]);
  const [editingCase, setEditingCase] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchCases = useCallback(async (maxAmount = null) => {
    setLoading(true);
    setError('');
    try {
      let url = API_URL;
      if (maxAmount !== null) {
        url += `?maxAmount=${maxAmount}`;
      }
      const response = await axios.get(url);
      setCases(response.data);
    } catch (err) {
      setError('Eroare la incarcarea cazurilor: ' + err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  // Load all cases on mount
  useEffect(() => {
    fetchCases();
  }, [fetchCases]);

  const handleAdd = async (charityCase) => {
    try {
      const response = await axios.post(API_URL, charityCase);
      setCases([...cases, response.data]);
    } catch (err) {
      setError('Eroare la adaugare: ' + err.message);
    }
  };

  const handleUpdate = async (charityCase) => {
    try {
      const response = await axios.put(`${API_URL}/${charityCase.id}`, charityCase);
      setCases(cases.map(c => c.id === charityCase.id ? response.data : c));
      setEditingCase(null);
    } catch (err) {
      setError('Eroare la modificare: ' + err.message);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Sigur vrei sa stergi acest caz?')) {
      try {
        await axios.delete(`${API_URL}/${id}`);
        setCases(cases.filter(c => c.id !== id));
      } catch (err) {
        setError('Eroare la stergere: ' + err.message);
      }
    }
  };

  const handleEdit = (charityCase) => {
    setEditingCase(charityCase);
  };

  const handleCancelEdit = () => {
    setEditingCase(null);
  };

  const handleFilter = (maxAmount) => {
    fetchCases(maxAmount);
  };

  return (
    <div className="app">
      <header>
        <h1>Gestionare Cazuri Caritabile</h1>
      </header>

      {error && <div className="error">{error}</div>}

      <main>
        <section className="form-section">
          <CharityForm
            onSubmit={editingCase ? handleUpdate : handleAdd}
            editingCase={editingCase}
            onCancel={handleCancelEdit}
          />
        </section>

        <section className="filter-section">
          <CharityFilter onFilter={handleFilter} />
        </section>

        <section className="table-section">
          {loading ? <p>Se incarca...</p> : <CharityTable cases={cases} onEdit={handleEdit} onDelete={handleDelete} />}
        </section>
      </main>
    </div>
  );
}

export default App;
