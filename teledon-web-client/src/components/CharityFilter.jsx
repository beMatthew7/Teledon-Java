import { useState } from 'react';

const CharityFilter = ({ onFilter }) => {
  const [maxAmount, setMaxAmount] = useState('');

  const handleFilter = (e) => {
    e.preventDefault();
    onFilter(maxAmount ? parseFloat(maxAmount) : null);
  };

  const handleClear = () => {
    setMaxAmount('');
    onFilter(null);
  };

  return (
    <form onSubmit={handleFilter} className="charity-filter">
      <h3>Filtreaza cazuri</h3>
      <div className="filter-group">
        <label htmlFor="maxAmount">Suma maxima:</label>
        <input
          type="number"
          id="maxAmount"
          value={maxAmount}
          onChange={(e) => setMaxAmount(e.target.value)}
          step="0.01"
          min="0"
          placeholder="Introdu suma maxima"
        />
      </div>
      <div className="filter-actions">
        <button type="submit">Filtreaza</button>
        <button type="button" onClick={handleClear}>Reset</button>
      </div>
    </form>
  );
};

export default CharityFilter;
