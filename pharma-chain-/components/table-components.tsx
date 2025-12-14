import type React from "react"

export const Table = ({ children, className = "" }: { children: React.ReactNode; className?: string }) => (
  <div className={`w-full overflow-auto ${className}`}>
    <table className="w-full border-collapse">{children}</table>
  </div>
)

export const TableHeader = ({ children }: { children: React.ReactNode }) => (
  <thead className="bg-slate-50/80 border-b border-slate-200 text-slate-700">{children}</thead>
)

export const TableBody = ({ children }: { children: React.ReactNode }) => (
  <tbody className="divide-y divide-slate-200/80 bg-white/60">{children}</tbody>
)

export const TableRow = ({ children, className = "" }: { children: React.ReactNode; className?: string }) => (
  <tr className={`transition hover:bg-teal-50/40 ${className}`}>{children}</tr>
)

export const TableHead = ({ children, className = "" }: { children: React.ReactNode; className?: string }) => (
  <th className={`px-4 py-3 text-left text-sm font-semibold text-slate-700 ${className}`}>{children}</th>
)

export const TableCell = ({ children, className = "" }: { children: React.ReactNode; className?: string }) => (
  <td className={`px-4 py-3 text-sm text-slate-900 ${className}`}>{children}</td>
)
