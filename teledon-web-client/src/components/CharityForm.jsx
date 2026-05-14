import { useState, useEffect } from 'react';

const CharityForm = ({ onSubmit, editingCase, onCancel }) => {
  const [name, setName] = useState('');
  const [totalAmount, setTotalAmount] = useState('');

  useEffect(() => {
    if (editingCase) {
      setName(editingCase.name);
      setTotalAmount(editingCase.totalAmount);
    } else {
      setName('');
      setTotalAmount('');
    }
  }, [editingCase]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const charityCase = {
      name,
      totalAmount: parseFloat(totalAmount)
    };
    if (editingCase) {
      charityCase.id = editingCase.id;
    }
    onSubmit(charityCase);
    if (!editingCase) {
      setName('');
      setTotalAmount('');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="charity-form">
      <h2>{editingCase ? 'Editează Caz Caritabil' : 'Adaugă Caz Caritabil'}</h2>
      <div className="form-group">
        <label htmlFor="name">Nume:</label>
        <input
          type="text"
          id="name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
      </div>
      <div className="form-group">
        <label htmlFor="totalAmount">Sumă:</label>
        <input
          type="number"
          id="totalAmount"
          value={totalAmount}
          onChange={(e) => setTotalAmount(e.target.value)}
          step="0.01"
          min="0"
          required
        />
      </div>
      <div className="form-actions">
        <button type="submit">
          {editingCase ? 'Salvează' : 'Adaugă'}
        </button>
        {editingCase && (
          <button type="button" onClick={onCancel}>
            Anulează
          </button>
        )}
      </div>
    </form>
  );
};

export default CharityForm;
