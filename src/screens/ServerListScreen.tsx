import React, { useState } from 'react';
import { ArrowLeft, RefreshCw, Plus, Zap, Server, Loader, Shield, Link2 } from 'lucide-react';

export default function ServerListScreen() {
  const [servers, setServers] = useState([
    { id: 1, name: 'Frankfurt-01', protocol: 'VLESS', ping: 145, active: true, manualSettings: true },
    { id: 2, name: 'Amsterdam-03', protocol: 'Hysteria2', ping: 210, active: false, manualSettings: false },
    { id: 3, name: 'London-05', protocol: 'Trojan', ping: 320, active: false, manualSettings: true },
    { id: 4, name: 'Paris-02', protocol: 'VMess', ping: 85, active: false, manualSettings: false },
  ]);
  
  const [testing, setTesting] = useState(false);
  const [syncing, setSyncing] = useState(false);

  const handleTestAll = () => {
    setTesting(true);
    setServers(servers.map(s => ({ ...s, ping: null })));
    setTimeout(() => {
      setServers([
        { id: 1, name: 'Frankfurt-01', protocol: 'VLESS', ping: 120, active: true, manualSettings: true },
        { id: 2, name: 'Amsterdam-03', protocol: 'Hysteria2', ping: 190, active: false, manualSettings: false },
        { id: 3, name: 'London-05', protocol: 'Trojan', ping: 350, active: false, manualSettings: true },
        { id: 4, name: 'Paris-02', protocol: 'VMess', ping: 75, active: false, manualSettings: false },
      ]);
      setTesting(false);
    }, 2500);
  };

  const handleSync = () => {
    setSyncing(true);
    setTimeout(() => setSyncing(false), 2000);
  };

  const getPingColor = (ping) => {
    if (ping === null) return 'text-slate-500';
    if (ping < 150) return 'text-green-400';
    if (ping < 300) return 'text-yellow-400';
    return 'text-red-400';
  };

  return (
    <div className="flex flex-col h-screen bg-[#0a0f1e] text-slate-200 font-sans antialiased">
      <header className="flex justify-between items-center px-4 py-4 border-b border-slate-800/50">
        <div className="flex items-center space-x-3">
          <button className="p-2 rounded-lg hover:bg-slate-800 transition-colors">
            <ArrowLeft size={20} className="text-slate-400" />
          </button>
          <h1 className="text-lg font-semibold tracking-tight">Servers</h1>
        </div>
        <div className="flex items-center space-x-2">
          <button onClick={handleTestAll} disabled={testing} className="flex items-center space-x-2 px-3 py-2 rounded-lg bg-slate-800 hover:bg-slate-700 transition-colors disabled:opacity-50">
            {testing ? <Loader size={16} className="animate-spin text-teal-400" /> : <Zap size={16} className="text-teal-400" />}
            <span className="text-xs font-medium">Test all</span>
          </button>
          <button onClick={handleSync} disabled={syncing} className="p-2 rounded-lg bg-slate-800 hover:bg-slate-700 transition-colors">
            <RefreshCw size={16} className={`${syncing ? 'animate-spin text-amber-400' : 'text-slate-400'}`} />
          </button>
        </div>
      </header>

      <main className="flex-1 overflow-y-auto p-4 space-y-3">
        {servers.map((server) => (
          <div key={server.id} className={`flex items-center justify-between p-4 rounded-xl border transition-all duration-200 cursor-pointer ${server.active ? 'bg-[#111827] border-teal-500 shadow-lg shadow-teal-500/10' : 'bg-[#0f1525] border-slate-800 hover:border-slate-700'}`}>
            <div className="flex items-center space-x-4">
              <div className="flex flex-col space-y-1">
                <div className="flex items-center space-x-2">
                  <span className="font-medium text-slate-100">{server.name}</span>
                  {server.manualSettings && (
                    <div className="flex items-center space-x-1">
                      <Shield size={12} className="text-amber-400" />
                      <Link2 size={12} className="text-purple-400" />
                    </div>
                  )}
                </div>
                <span className="text-xs font-mono text-slate-500 bg-slate-800/50 px-2 py-0.5 rounded w-fit border border-slate-700">
                  {server.protocol}
                </span>
              </div>
            </div>
            <div className="flex flex-col items-end space-y-1 w-16">
              {testing && server.ping === null ? (
                <Loader size={16} className="animate-spin text-slate-500" />
              ) : (
                <span className={`font-mono text-lg tabular-nums ${getPingColor(server.ping)}`}>
                  {server.ping}<span className="text-xs ml-0.5">ms</span>
                </span>
              )}
              {server.ping !== null && (
                 <div className="w-2 h-2 rounded-full" style={{ backgroundColor: server.ping < 150 ? '#4ade80' : server.ping < 300 ? '#facc15' : '#f87171' }}></div>
              )}
            </div>
          </div>
        ))}
      </main>

      <button className="absolute bottom-24 right-6 w-14 h-14 rounded-full bg-[#2dd4bf] text-[#0a0f1e] flex items-center justify-center shadow-lg shadow-teal-500/30 hover:scale-105 transition-transform active:scale-95">
        <Plus size={28} />
      </button>
    </div>
  );
}
