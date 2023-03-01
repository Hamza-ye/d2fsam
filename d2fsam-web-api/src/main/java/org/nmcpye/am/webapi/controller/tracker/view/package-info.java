/**
 * Contains the view (JSON) models of tracker. Imports and exports share the
 * same model. This is deliberate as to ensure we keep the promise of an export
 * being importable. Import mappers in
 * {@link org.nmcpye.am.webapi.controller.tracker.imports} translate JSON
 * payloads (view models) to the {@link org.nmcpye.am.tracker.domain} which is
 * used internally. Export mappers in
 * {@link org.nmcpye.am.webapi.controller.tracker.export} translate from
 * {@link org.nmcpye.am} to the JSON payloads (view models) tracker exposes to
 * its users.
 */
package org.nmcpye.am.webapi.controller.tracker.view;
