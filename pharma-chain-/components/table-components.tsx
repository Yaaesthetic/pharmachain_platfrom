import type React from "react"

export const Table = ({ children, className = "" }: { children: React.ReactNode; className?: string }) => (
  <div className={`w-full overflow-auto ${className}`}>
    <table className="w-full border-collapse">{children}</table>
  </div>
)

export const TableHeader = ({ children }: { children: React.ReactNode }) => (
  <thead className="bg-gray-100 border-b-2 border-gray-200">{children}</thead>
)

export const TableBody = ({ children }: { children: React.ReactNode }) => (
  <tbody className="divide-y divide-gray-200">{children}</tbody>
)

export const TableRow = ({ children, className = "" }: { children: React.ReactNode; className?: string }) => (
  <tr className={`hover:bg-gray-50 ${className}`}>{children}</tr>
)

export const TableHead = ({ children, className = "" }: { children: React.ReactNode; className?: string }) => (
  <th className={`px-4 py-3 text-left text-sm font-semibold text-gray-700 ${className}`}>{children}</th>
)

export const TableCell = ({ children, className = "" }: { children: React.ReactNode; className?: string }) => (
  <td className={`px-4 py-3 text-sm text-gray-900 ${className}`}>{children}</td>
)
